package onipractice.mahmoud.com.fitnessapp.Models;

public class MessageModel {

    @Override
    public String toString() {
        return "MessageModel{" +
                "message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", time=" + time +
                ", seen=" + seen +
                ", from='" + from + '\'' +
                '}';
    }

    private String message, type;
    long time;
    boolean seen;
    private String from;

    public MessageModel(String message, boolean seen, long time, String type, String from) {

        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
        this.from = from;
    }

    public MessageModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
