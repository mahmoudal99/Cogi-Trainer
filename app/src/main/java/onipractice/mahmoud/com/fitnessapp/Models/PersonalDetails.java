package onipractice.mahmoud.com.fitnessapp.Models;

public class PersonalDetails {

    private static final String TAG = "PersonalDetails";

    private String height;
    private String weight;
    private String age;
    private String gender;

    public PersonalDetails(String height, String weight, String age, String gender){

        this.height = height;
        this.weight = weight;
        this.age = age;
        this.gender = gender;

    }

    public static String getTAG() {
        return TAG;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "PersonalDetails{" +
                "height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
