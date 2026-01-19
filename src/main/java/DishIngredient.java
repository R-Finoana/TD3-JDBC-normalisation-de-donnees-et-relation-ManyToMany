import java.util.Objects;

public class DishIngredient {
    private Dish dish;
    private Ingredient ingredient;
    private Double quantityRequired;
    private UnitTypeEnum unit;

    public DishIngredient() {
    }

    public DishIngredient(Dish dish, Ingredient ingredient, Double quantityRequired, UnitTypeEnum unit) {
        this.dish = dish;
        this.ingredient = ingredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Double getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(Double quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public UnitTypeEnum getUnit() {
        return unit;
    }

    public void setUnit(UnitTypeEnum unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DishIngredient that = (DishIngredient) o;
        return Objects.equals(dish, that.dish) && Objects.equals(ingredient, that.ingredient) && Objects.equals(quantityRequired, that.quantityRequired) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dish, ingredient, quantityRequired, unit, ingredient);
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                "dish=" + dish +
                ", ingredient=" + ingredient +
                ", quantityRequired=" + quantityRequired +
                ", unit=" + unit +
                '}';
    }
}
