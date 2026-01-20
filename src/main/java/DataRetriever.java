import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataRetriever {
    Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select dish.id as dish_id, dish.name as dish_name, dish_type, dish.selling_price as selling_price
                            from dish
                            where dish.id = ?;
                            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                dish.setSellingPrice(resultSet.getObject("selling_price") == null
                        ? null : resultSet.getDouble("selling_price"));
                List<DishIngredient> associations = findDishIngredientsByDishId(id);
                for(DishIngredient di : associations){
                    di.setDish(dish);
                }
                dish.setDishIngredients(associations);
                return dish;
            }
            dbConnection.closeConnection(connection);
            throw new RuntimeException("Dish not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DishIngredient> findDishIngredientsByDishId(Integer dishId){
        DBConnection dbConnection = new DBConnection();
        Connection connection = null;
        try{
            String sql = """
            SELECT di.quantity_required, di.unit,
                   i.id AS ingredient_id, i.name AS ingredient_name, i.price, i.category
            FROM dish_ingredient di
            JOIN ingredient i ON di.id_ingredient = i.id
            WHERE di.id_dish = ?
            """;
            connection = dbConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, dishId);

            ResultSet rs = stmt.executeQuery();
            List<DishIngredient> dishIngredients = new ArrayList<>();

            while(rs.next()){
                DishIngredient di = new DishIngredient();
                di.setQuantityRequired(rs.getDouble("quantity_required"));
                di.setUnit(UnitTypeEnum.valueOf(rs.getString("unit")));

                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("ingredient_id"));
                ingredient.setName(rs.getString("ingredient_name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                di.setIngredient(ingredient);

                dishIngredients.add(di);
            }
            return dishIngredients;
        } catch (SQLException e){
            throw new RuntimeException("Error retrieving DishIngredient for the dish "+dishId, e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    Dish saveDish(Dish toSave) {
        String upsertDishSql = """
                    INSERT INTO dish (id, name, dish_type, selling_price)
                    VALUES (?, ?, ?::dish_type, ?)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        dish_type = EXCLUDED.dish_type,
                        selling_price = EXCLUDED.selling_price
                    RETURNING id
                """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "dish", "id"));
                }
                ps.setString(2, toSave.getName());
                ps.setString(3, toSave.getDishType().name());
                if (toSave.getSellingPrice() != null) {
                    ps.setDouble(4, toSave.getSellingPrice());
                } else {
                    ps.setNull(4, Types.DOUBLE);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }

            detachDishIngredients(conn, dishId);
            attachDishIngredients(conn, dishId, toSave.getDishIngredients());

            conn.commit();
            return findDishById(dishId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return List.of();
        }
        List<Ingredient> savedIngredients = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            String insertSql = """
                        INSERT INTO ingredient (id, name, price, category)
                        VALUES (?, ?, ?, ?::ingredient_category)
                        RETURNING id
                    """;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : newIngredients) {
                    if (ingredient.getId() != null) {
                        ps.setInt(1, ingredient.getId());
                    } else {
                        ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                    }
                    ps.setString(2, ingredient.getName());
                    ps.setString(3, ingredient.getCategory().name());
                    ps.setDouble(4, ingredient.getPrice());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        int generatedId = rs.getInt(1);
                        ingredient.setId(generatedId);
                        savedIngredients.add(ingredient);
                    }
                }
                conn.commit();
                return savedIngredients;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }


    private void detachDishIngredients(Connection conn, Integer dishId)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM dish_ingredient WHERE id_dish = ?")) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void attachDishIngredients(Connection conn, Integer dishId, List<DishIngredient> dishIngredients)
            throws SQLException {

        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return;
        }

        String attachSql = """
                    INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit)
                    VALUES (?, ?, ?, ?::unit_type)
                """;

        try (PreparedStatement ps = conn.prepareStatement(attachSql)) {
            for (DishIngredient di : dishIngredients) {
                ps.setInt(1, dishId);
                ps.setInt(2, di.getIdIngredient());
                ps.setDouble(3, di.getQuantityRequired());
                ps.setObject(4, di.getUnit());
                ps.addBatch(); // Can be substitute ps.executeUpdate() but bad performance
            }
            ps.executeBatch();
        }
    }

    private List<Ingredient> findIngredientByDishId(Integer idDish) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select ingredient.id, ingredient.name, ingredient.price, ingredient.category, ingredient.required_quantity
                            from ingredient where id_dish = ?;
                            """);
            preparedStatement.setInt(1, idDish);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(resultSet.getInt("id"));
                ingredient.setName(resultSet.getString("name"));
                ingredient.setPrice(resultSet.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(resultSet.getString("category")));
                Object requiredQuantity = resultSet.getObject("required_quantity");
                ingredient.setQuantity(requiredQuantity == null ? null : resultSet.getDouble("required_quantity"));
                ingredients.add(ingredient);
            }
            dbConnection.closeConnection(connection);
            return ingredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private String getSerialSequenceName(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sql = "SELECT pg_get_serial_sequence(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException(
                    "Any sequence found for " + tableName + "." + columnName
            );
        }
        updateSequenceNextValue(conn, tableName, columnName, sequenceName);

        String nextValSql = "SELECT nextval(?)";

        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String setValSql = String.format(
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName
        );

        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }
}
