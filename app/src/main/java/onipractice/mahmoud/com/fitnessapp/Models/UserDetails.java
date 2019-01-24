package onipractice.mahmoud.com.fitnessapp.Models;

public class UserDetails {

    private static final String TAG = "UserDtails";

    private String firstname;
    private String lastname;
    private String PrefChosen;
    private String Preference;
    private String email;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;

    @Override
    public String toString() {
        return "UserDetails{" +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", PrefChosen='" + PrefChosen + '\'' +
                ", Preference='" + Preference + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public UserDetails(String firstname, String lastname, String PrefChosen, String Preference, String email, String image){
        this.firstname = firstname;
        this.lastname = lastname;
        this.PrefChosen = PrefChosen;
        this.Preference = Preference;
        this.email = email;
        this.image = image;

    }

    public static String getTAG() {
        return TAG;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPreference() {
        return Preference;
    }

    public void setPreference(String preference) {
        Preference = preference;
    }

    public String getPrefChosen() {
        return PrefChosen;
    }

    public void setPrefChosen(String prefChosen) {
        PrefChosen = prefChosen;
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

}
