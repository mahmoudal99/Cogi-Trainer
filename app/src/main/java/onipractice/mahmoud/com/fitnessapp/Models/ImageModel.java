package onipractice.mahmoud.com.fitnessapp.Models;

public class ImageModel {

    private String image;

    public ImageModel(String image){
        this.image = image;
    }

    public ImageModel(){}

    @Override
    public String toString() {
        return "ImageModel{" +
                "image='" + image + '\'' +
                '}';
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
