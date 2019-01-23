package onipractice.mahmoud.com.fitnessapp.Models;

public class TrainingDayModel {

    private String day;
    private String time;
    private String workout;

    public TrainingDayModel(String day, String time, String workout){

        this.day = day;
        this.time = time;
        this.workout = workout;

    }

    public TrainingDayModel(){ }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWorkout() {
        return workout;
    }

    public void setWorkout(String workout) {
        this.workout = workout;
    }

    @Override
    public String toString() {
        return "TrainingDayModel{" +
                "day='" + day + '\'' +
                ", time='" + time + '\'' +
                ", workout='" + workout + '\'' +
                '}';
    }
}
