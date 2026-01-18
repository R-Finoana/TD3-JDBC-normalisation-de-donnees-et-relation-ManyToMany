import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private Double sellingPrice;
    private String name;
    private DishTypeEnum dishType;
    private List<DishIngredient> dishIngredients;

    public Double getPrice() {
        return sellingPrice;
    }

    public void setPrice(Double price) {
        this.sellingPrice = sellingPrice;
    }

    public Double getDishCost() {
        double totalPrice = 0;
        for (int i = 0; i < dishIngredients.size(); i++) {
            Double quantity = dishIngredients.get(i).getQuantityRequired();
            if(quantity == null) {
                throw new RuntimeException("...");
            }
            totalPrice = totalPrice + dishIngredients.get(i).getPrice() * quantity;
        }
        return totalPrice;
    }

    public Dish() {
    }

    public Dish(Integer id, Double sellingPrice, String name, DishTypeEnum dishType, List<DishIngredient> dishIngredients) {
        this.id = id;
        this.sellingPrice = sellingPrice;
        this.name = name;
        this.dishType = dishType;
        this.dishIngredients = dishIngredients;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public List<DishIngredient> getDishIngredients() {
        return dishIngredients;
    }

    public void setDishIngredients(List<DishIngredient> dishIngredients) {
        this.dishIngredients = dishIngredients;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name) && dishType == dish.dishType && Objects.equals(ingredients, dish.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, dishIngredients);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", price=" + sellingPrice +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", ingredients=" + dishIngredients +
                '}';
    }

    public Double getGrossMargin() {
        if (sellingPrice == null) {
            throw new RuntimeException("Price is null");
        }
        return sellingPrice - getDishCost();
    }
}
