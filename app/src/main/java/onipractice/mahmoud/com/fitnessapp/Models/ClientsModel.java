package onipractice.mahmoud.com.fitnessapp.Models;

public class ClientsModel {

    public String id;

    public ClientsModel(){

    }

    public ClientsModel(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ClientsModel{" +
                "id='" + id + '\'' +
                '}';
    }
}

