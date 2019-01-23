package onipractice.mahmoud.com.fitnessapp.Models;

public class FoodItemModel {

    private String foodName;

    public FoodItemModel(String foodName){

        this.foodName = foodName;
    }

    public FoodItemModel(){}

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    @Override
    public String toString() {
        return "FoodItemModel{" +
                "foodName='" + foodName + '\'' +
                '}';
    }
}
