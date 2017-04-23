package co.tagtalk.winemate;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by Zhaoyu on 2016/9/29.
 */

public class TimeStamp {

    private Calendar c;

    public TimeStamp() {
        this.c = Calendar.getInstance();
    }

    public String getCurrentDate() {
        //final String[] months = {"", "Jan.", "Feb.", "Mar.", "Apr.", "May.", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."};

        int date = c.get(Calendar.DATE);
        String currentDate;

        int month = c.get(Calendar.MONTH) + 1;

        if (date < 10) {
            if (month < 10) {
                currentDate = String.valueOf(c.get(Calendar.YEAR)) + "/0" + String.valueOf(c.get(Calendar.MONTH) + 1) + "/0" + String.valueOf(c.get(Calendar.DATE));
            } else {
                currentDate = String.valueOf(c.get(Calendar.YEAR)) + "/" + String.valueOf(c.get(Calendar.MONTH) + 1) + "/0" + String.valueOf(c.get(Calendar.DATE));
            }
        } else {
            if (month < 10) {
                currentDate = String.valueOf(c.get(Calendar.YEAR)) + "/0" + String.valueOf(c.get(Calendar.MONTH) + 1) + "/" + String.valueOf(c.get(Calendar.DATE));
            } else {
                currentDate = String.valueOf(c.get(Calendar.YEAR)) + "/" + String.valueOf(c.get(Calendar.MONTH) + 1) + "/" + String.valueOf(c.get(Calendar.DATE));
            }
        }

        return currentDate;
    }

    public String getCurrentTime() {
        Integer hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        String time = "";
        String minute;

        if (c.get(Calendar.MINUTE) < 10) {
            minute = "0" + c.get(Calendar.MINUTE);
        } else {
            minute = "" + c.get(Calendar.MINUTE);
        }

        if(hourOfDay >= 12) {
            time = c.get(Calendar.HOUR) + ":" + minute + " PM";
        } else {
            if(hourOfDay!=0) {
                time = c.get(Calendar.HOUR) + ":" + minute + " AM";
            } else {
                time = "12" + ":" + minute + " AM";
            }
        }

        Log.v("getCurrentTime", time);
        return time;
    }

}
