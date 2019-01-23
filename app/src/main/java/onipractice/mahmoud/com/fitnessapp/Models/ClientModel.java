package onipractice.mahmoud.com.fitnessapp.Models;

public class ClientModel {

    public String traineeId;

    public String getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(String traineeId) {
        this.traineeId = traineeId;
    }

    public ClientModel(){

    }

    public ClientModel(String traineeId){
        this.traineeId = traineeId;
    }

    @Override
    public String toString() {
        return "ClientModel{" +
                "traineeId='" + traineeId + '\'' +
                '}';
    }
}

