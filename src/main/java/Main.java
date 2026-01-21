import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Log before changes
        DataRetriever dataRetriever = new DataRetriever();
        Dish dish = dataRetriever.findDishById(4

        );
        System.out.println(dish);

        // Log after changes
//        dish.setIngredients(List.of(new Ingredient(1), new Ingredient(2)));
//        Dish newDish = dataRetriever.saveDish(dish);
//        System.out.println(newDish);

        // Ingredient creations
        //List<Ingredient> createdIngredients = dataRetriever.createIngredients(List.of(new Ingredient(null, "Fromage", CategoryEnum.DAIRY, 1200.0)));
        //System.out.println(createdIngredients);

        System.out.println("\n--FindDishById and getDishCost --");
        try {
            Dish salade = dataRetriever.findDishById(1);
            System.out.println("Salade fraîche cost: " + salade.getDishCost());//should be 250
            System.out.println(salade);
        } catch (RuntimeException e) {
            System.out.println("Exception for Salade: " + e.getMessage());
        }

        System.out.println("\n--Gross margin test--");
        try {
            Dish poulet = dataRetriever.findDishById(2);
            System.out.println("Poulet grillé margin: " + poulet.getGrossMargin());  //should be 7500
        } catch (RuntimeException e) {
            System.out.println("Exception for Poulet: " + e.getMessage());
        }

        try {
            Dish riz = dataRetriever.findDishById(3);
            System.out.println("Riz margin: " + riz.getGrossMargin());
        } catch (RuntimeException e) {
            System.out.println("Exception for Riz: " + e.getMessage());
        }

        System.out.println("\n--Test for saveDish--");
        Dish updatedSalade = new Dish();
        updatedSalade.setId(1);
        updatedSalade.setName("Salade fraîche updated");
        updatedSalade.setDishType(DishTypeEnum.STARTER);
        updatedSalade.setSellingPrice(4000.0);
        Ingredient tomate = new Ingredient();
        tomate.setId(1);
        tomate.setName("Tomate");
        tomate.setPrice(600.0);
        tomate.setCategory(CategoryEnum.VEGETABLE);
        Ingredient salade = new Ingredient();
        tomate.setId(2);
        tomate.setName("Salade");
        tomate.setPrice(600.0);
        tomate.setCategory(CategoryEnum.VEGETABLE);
        System.out.println("\n--Add dishIngredient--");
        List<DishIngredient> dis = new ArrayList<>();
        dis.add(new DishIngredient(updatedSalade, tomate, 0.25, UnitTypeEnum.KG));
        dis.add(new DishIngredient(updatedSalade, salade, 0.10, UnitTypeEnum.KG));
        updatedSalade.setDishIngredients(dis);

        Dish saved = dataRetriever.saveDish(updatedSalade);
        System.out.println("Saved dish: " + saved);
        System.out.println("New cost: " + saved.getDishCost());
        System.out.println("New margin: " + saved.getGrossMargin());
    }
}
