package onipractice.mahmoud.com.fitnessapp.Models;

public class TrainerModel {

    public String firstname;
    public String lastname;

    public TrainerModel() {

    }

    public TrainerModel(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getName() {
        return firstname;
    }

    public void setName(String name) {
        this.firstname = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "name='" + firstname + '\'' +
                '}';
    }
}

