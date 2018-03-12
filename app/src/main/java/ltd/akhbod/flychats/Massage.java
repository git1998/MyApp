package ltd.akhbod.flychats;

/**
 * Created by ibm on 01-02-2018.
 */

public class Massage {

    private String massage,type,from;
    boolean seen;
    long time;

    public Massage(String massage, String type, boolean seen, long time,String from) {
        this.massage = massage;
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.from=from;
    }


    public Massage(){


    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
