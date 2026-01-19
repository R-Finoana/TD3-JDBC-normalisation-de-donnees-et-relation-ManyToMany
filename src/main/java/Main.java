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
            System.out.println("Salade fraîche cost: " + salade.getDishCost());
            System.out.println(salade);
        } catch (RuntimeException e) {
            System.out.println("Exception for Salade: " + e.getMessage());
        }
    }
}
