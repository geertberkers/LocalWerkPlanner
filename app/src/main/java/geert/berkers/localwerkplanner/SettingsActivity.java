package geert.berkers.localwerkplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by Geert on 23-9-2015
 */
public class SettingsActivity extends ActionBarActivity {

    private TextView endTime;
    private TextView startTime;

    private RadioButton rbDayFirst;
    private RadioButton rbMonthFirst;

    private ImageButton editEndTime;
    private ImageButton editStartTime;

    private String currentDateFormat;
    private String favStartHour;
    private String favStartMinute;
    private String favEndHour;
    private String favEndMinute;

    private String oldfavStartHour;
    private String oldfavStartMinute;
    private String oldfavEndHour;
    private String oldfavEndMinute;

    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    private boolean dayfirst;

    private static final int TIME_DIALOG_END = 888;
    private static final int TIME_DIALOG_START = 999;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        initControls();
        setSettings();
        setOnClickListeners();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.settings);
    }

    private void initControls() {
        endTime = (TextView) findViewById(R.id.txtEndTime);
        startTime = (TextView) findViewById(R.id.txtStartTime);
        rbDayFirst = (RadioButton) findViewById(R.id.rbDayFirst);
        rbMonthFirst = (RadioButton) findViewById(R.id.rbMonthFirst);
        editEndTime = (ImageButton) findViewById(R.id.editEndTimeFav);
        editStartTime = (ImageButton) findViewById(R.id.editStartTimeFav);
    }

    private void setSettings() {
        currentDateFormat = sharedPref.getString("dateFormat", "dd-MM-yyyy");
        favStartHour = sharedPref.getString("fav_start_time_hour", "18");
        favStartMinute = sharedPref.getString("fav_start_time_minute", "00");
        favEndHour = sharedPref.getString("fav_end_time_hour", "20");
        favEndMinute = sharedPref.getString("fav_end_time_minute", "30");

        oldfavStartHour = favStartHour;
        oldfavStartMinute = favStartMinute;
        oldfavEndHour = favEndHour;
        oldfavEndMinute = favEndMinute;

        endTime.setText(new StringBuilder().append(favEndHour).append(":").append(favEndMinute));
        startTime.setText(new StringBuilder().append(favStartHour).append(":").append(favStartMinute));

        if(rbDayFirst.getText().toString().equals(currentDateFormat)){
            dayfirst = true;
            rbDayFirst.setChecked(true);
            rbMonthFirst.setChecked(false);
        } else {
            dayfirst = false;
            rbDayFirst.setChecked(false);
            rbMonthFirst.setChecked(true);
        }

        try { startHour = Integer.valueOf(favStartHour); }
        catch(Exception ex) { startHour = Integer.valueOf(favStartHour.substring(1)); }

        try { startMinute = Integer.valueOf(favStartMinute); }
        catch(Exception ex) { startMinute = Integer.valueOf(favStartMinute.substring(1)); }

        try { endHour = Integer.valueOf(favEndHour); }
        catch(Exception ex) { endHour = Integer.valueOf(favEndHour.substring(1)); }

        try { endMinute = Integer.valueOf(favEndMinute); }
        catch(Exception ex) { endMinute = Integer.valueOf(favEndMinute.substring(1)); }
    }

    private void setOnClickListeners(){

        rbDayFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbDayFirst.setChecked(true);
                rbMonthFirst.setChecked(false);
            }
        });
        rbMonthFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbDayFirst.setChecked(false);
                rbMonthFirst.setChecked(true);
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_START);
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_END);
            }
        });

        editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_START);
            }
        });

        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_END);
            }
        });
    }

    private boolean settingsChanged() {
        if(rbDayFirst.isChecked() == dayfirst
                && oldfavEndHour.equals(endTime.getText().toString().substring(0, 2))
                && oldfavEndMinute.equals(endTime.getText().toString().substring(3, 5))
                && oldfavStartHour.equals(startTime.getText().toString().substring(0, 2))
                && oldfavStartMinute.equals(startTime.getText().toString().substring(3, 5))){
            return false;
        } else {
            return true;
        }
    }

    public void onCancelClick(View v) {
        if (settingsChanged()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.changes_found);
            alertDialog.setMessage(R.string.save_changes);
            alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    save();
                }
            });
            alertDialog.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    //Do nothing
                }
            });
            alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.setIcon(R.drawable.ic_info_outline_black_36dp);
            alertDialog.show();
        } else {
            finish();
        }
    }

    public void onSaveClick(View v){
        save();
    }

    private void save() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putString("fav_start_time_hour", startTime.getText().toString().substring(0, 2));
        editor.putString("fav_start_time_minute",startTime.getText().toString().substring(3, 5));
        editor.putString("fav_end_time_hour", endTime.getText().toString().substring(0, 2));
        editor.putString("fav_end_time_minute", endTime.getText().toString().substring(3, 5));

        if(rbDayFirst.isChecked()) {
            editor.putString("dateFormat",rbDayFirst.getText().toString());
        } else {
            editor.putString("dateFormat",rbMonthFirst.getText().toString());
        }
        editor.apply();

        Toast.makeText(this, R.string.setting_saved, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_START:
                return new TimePickerDialog(this, startTimePickerListener, startHour, startMinute, true);
            case TIME_DIALOG_END:
                return new TimePickerDialog(this, endTimePickerListener, endHour, endMinute, true);
        }
        return null;
    }

    private final TimePickerDialog.OnTimeSetListener startTimePickerListener =  new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            if(selectedHour < 10){
                startHour = selectedHour;
                startMinute = selectedMinute;
                favStartHour = "0" + selectedHour;
            } else { favStartHour = String.valueOf(selectedHour); }
            if(selectedMinute < 10) {
                favStartMinute = "0"+ selectedMinute;
            } else { favStartMinute = String.valueOf(selectedMinute); }
            startTime.setText(new StringBuilder().append(favStartHour).append(":").append(favStartMinute));
            removeDialog(TIME_DIALOG_START);
        }
    };

    private final TimePickerDialog.OnTimeSetListener endTimePickerListener =  new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            if(selectedHour < 10){
                endHour = selectedHour;
                endMinute = selectedMinute;
                favEndHour = "0" + selectedHour;
            } else { favEndHour = String.valueOf(selectedHour); }

            if(selectedMinute < 10) {
                favEndMinute = "0"+ selectedMinute;
            } else { favEndMinute = String.valueOf(selectedMinute); }

            endTime.setText(new StringBuilder().append(favEndHour).append(":").append(favEndMinute));
            removeDialog(TIME_DIALOG_END);
        }
    };
}
