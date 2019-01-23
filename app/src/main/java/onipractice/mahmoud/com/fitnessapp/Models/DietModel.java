package onipractice.mahmoud.com.fitnessapp.Models;

public class DietModel {

    private String mealType;
    private String food;
    private String day;

    public DietModel(String mealType, String food, String day){
        this.mealType = mealType;
        this.food = food;
        this.day = day;

    }

    public DietModel(){}

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }


}
