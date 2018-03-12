package ltd.akhbod.flychats;

import android.util.Log;

/**
 * Created by ibm on 28-01-2018.
 */

public class Users {
    private String name, status, thumbimage;


    public Users(){

        Log.i("abhi","users2");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbimage() {
        return thumbimage;
    }

    public void setThumbimage(String thumbimage) {
        this.thumbimage = thumbimage;
    }
}
