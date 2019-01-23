package onipractice.mahmoud.com.fitnessapp.Models;

public class TrainerPersonalDetails {

    private static final String TAG = "TrainerPersonalDetails";

    private String education;
    private String specialitiesString;
    private String availibility;
    private String gender;
    private String firstname;
    private String lastname;

    public TrainerPersonalDetails(String firstname, String lastname, String education, String specialitiesString, String availibility, String gender){

        this.firstname = firstname;
        this.lastname = lastname;
        this.education = education;
        this.availibility = availibility;
        this.specialitiesString = specialitiesString;
        this.gender = gender;

    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public static String getTAG() {
        return TAG;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSpecialitiesString() {
        return specialitiesString;
    }

    public void setSpecialitiesString(String specialitiesString) {
        this.specialitiesString = specialitiesString;
    }

    public String getAvailibility() {
        return availibility;
    }

    public void setAvailibility(String availibility) {
        this.availibility = availibility;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "TrainerPersonalDetails{" +
                "education='" + education + '\'' +
                ", specialitiesString='" + specialitiesString + '\'' +
                ", availibility='" + availibility + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
