package geert.berkers.localwerkplanner;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Geert on 11-9-2015
 */
public class Work implements Parcelable, Comparable<Work>{

    private Date date;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private boolean past;
    private DateFormat df;

    public Work(Date date, String startTime, String endTime) {
        this.date = date;
        this.df = new SimpleDateFormat("dd-MM-yyyy");

        DateFormat day = new SimpleDateFormat("EEEE");
        this.dayOfWeek = day.format(date).substring(0, 1).toUpperCase() + day.format(date).substring(1);

        String dateString = df.format(Calendar.getInstance().getTime());

        Date currentDate = MainActivity.parseDate(dateString);

        past = false;

        if (currentDate != null) {
            if (date.before(currentDate)) {
                past = true;
            }
            else if (date.equals(currentDate)) {

                DateFormat time = new SimpleDateFormat("HH:mm");
                String timeString = time.format(Calendar.getInstance().getTime());

                if (Integer.valueOf(timeString.substring(0, 2)) >= Integer.valueOf(endTime.substring(0, 2))) {
                    if (Integer.valueOf(timeString.substring(3)) > Integer.valueOf(endTime.substring(3))) {
                        past = true;
                    }
                }
            }
        }

        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Work getWork() {
        return this;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getWorkString(String dateFormat) {

        DateFormat showDateFormat = new SimpleDateFormat(dateFormat);
        return showDateFormat.format(date) + "       " + startTime + "-" + endTime;
    }

    public String getDate(boolean yearFirst) {
        DateFormat f;
        if(yearFirst) {
            f = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            f = new SimpleDateFormat("dd-MM-yyyy");
        }
        return f.format(date);
    }

    public boolean getPastBoolean() {
        return past;
    }

    public Work(Parcel read){
        this.past = false;
        this.date = MainActivity.parseDate(read.readString());
        this.dayOfWeek = read.readString();
        this.startTime = read.readString();
        this.endTime = read.readString();
        this.df = new SimpleDateFormat("dd-MM-yyyy");
        if(read.readString().equals("Y")) { past = true; }
    }

    public static final Parcelable.Creator<Work> CREATOR =
            new Parcelable.Creator<Work>(){

                @Override
                public Work createFromParcel(Parcel source) {
                    return new Work(source);
                }

                @Override
                public Work[] newArray(int size) {
                    return new Work[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(df.format(date));
        arg0.writeString(dayOfWeek);
        arg0.writeString(startTime);
        arg0.writeString(endTime);
        if(past)  { arg0.writeString("Y"); }
        if(!past) { arg0.writeString("N"); }
    }

    @Override
    public int compareTo(@NonNull Work otherWork) {
        return this.getWorkString("dd-MM-yyyy").compareTo(otherWork.getWorkString("dd-MM-yyyy"));
    }

    @Override
    public String toString() {
        return "Work [date=" + df.format(date) + ", startTime=" + startTime + ", endTime=" + endTime + "]";
    }
}
