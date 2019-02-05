package onipractice.mahmoud.com.fitnessapp.Models;

public class MyTrainerModel {

    public String date;
    public String image;

    @Override
    public String toString() {
        return "MyTrainerModel{" +
                "date='" + date + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public MyTrainerModel() {

    }

    public MyTrainerModel(String firstname) {
        this.date = firstname;
    }

    public String getName() {
        return date;
    }

    public void setName(String name) {
        this.date = name;
    }

}

