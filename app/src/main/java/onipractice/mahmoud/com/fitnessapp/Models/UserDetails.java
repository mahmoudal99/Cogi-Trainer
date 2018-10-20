package onipractice.mahmoud.com.fitnessapp.Models;

public class UserDetails {

    private static final String TAG = "UserDtails";

    private String username;
    private String firstname;
    private String lastname;
    private String PrefChosen;
    private String Preference;

    public UserDetails(String username, String firstname, String lastname, String PrefChosen, String Preference){
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.PrefChosen = PrefChosen;
        this.Preference = Preference;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public String toString() {
        return "UserDetails{" +
                "username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}
