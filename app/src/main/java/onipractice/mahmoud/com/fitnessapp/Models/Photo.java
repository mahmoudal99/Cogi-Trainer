package onipractice.mahmoud.com.fitnessapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 7/29/2017.
 */

public class Photo implements Parcelable {

    private String image_path;
    private String photo_id;
    private String user_id;

    public Photo() { }

    public Photo(String image_path, String photo_id, String user_id) {
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;

    }

    protected Photo(Parcel in) {

        image_path = in.readString();
        photo_id = in.readString();
        user_id = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(image_path);
        dest.writeString(photo_id);
        dest.writeString(user_id);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public static Creator<Photo> getCREATOR() {
        return CREATOR;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
