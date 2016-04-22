package geert.berkers.localwerkplanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Geert on 19-4-2016
 */

public class TimePreference extends DialogPreference {

    private int hour = 0;
    private int minute = 0;
    private TimePicker picker = null;

    public TimePreference(Context ctxt) {
        this(ctxt, null);
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.setCurrentHour(hour);
        picker.setCurrentMinute(minute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            hour = picker.getCurrentHour();
            minute = picker.getCurrentMinute();

            String time = (String) getSummary();
            setSummary(time);

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            //time = defaultValue.toString();
            if (defaultValue == null) {
                time = timeFormat.format(new Date());
            } else {
                time = defaultValue.toString();
            }
        }

        String[] splitTime = time.split(":");
        hour = Integer.parseInt(splitTime[0]);
        minute = Integer.parseInt(splitTime[1]);

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        return createTimeStringFromInt(hour) + ":" + createTimeStringFromInt(minute);
    }

    private String createTimeStringFromInt(int time) {

        if (time < 10) {
            return "0" + time;
        } else {
            return String.valueOf(time);
        }
    }
}