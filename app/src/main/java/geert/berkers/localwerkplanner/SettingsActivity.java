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

    private TextView txtStartAMPM;
    private TextView txtEndAMPM;
    private TextView endTime;
    private TextView startTime;

    private RadioButton rb24H;
    private RadioButton rbAMPM;
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
    private boolean startAM;
    private boolean startPM;
    private boolean endAM;
    private boolean endPM;

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
        txtStartAMPM = (TextView) findViewById(R.id.txtAM);
        txtEndAMPM = (TextView) findViewById(R.id.txtAM1);
        rb24H = (RadioButton) findViewById(R.id.rb24H);
        rbAMPM = (RadioButton) findViewById(R.id.rbAMPM);
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

        startAM = sharedPref.getBoolean("startAM", false);
        startPM = sharedPref.getBoolean("startPM", false);
        endAM = sharedPref.getBoolean("endAM", false);
        endPM = sharedPref.getBoolean("endPM", false);

        oldfavStartHour = favStartHour;
        oldfavStartMinute = favStartMinute;
        oldfavEndHour = favEndHour;
        oldfavEndMinute = favEndMinute;

        setRBDate();
        setRBTimeFormat();
        setTimes();
    }

    private void setTimes() {
        try { startHour = Integer.valueOf(favStartHour); }
        catch(Exception ex) { startHour = Integer.valueOf(favStartHour.substring(0,1)); }

        try { startMinute = Integer.valueOf(favStartMinute); }
        catch(Exception ex) { startMinute = Integer.valueOf(favStartMinute.substring(0,1)); }

        try { endHour = Integer.valueOf(favEndHour); }
        catch(Exception ex) { endHour = Integer.valueOf(favEndHour.substring(0,1)); }

        try { endMinute = Integer.valueOf(favEndMinute); }
        catch(Exception ex) { endMinute = Integer.valueOf(favEndMinute.substring(0,1)); }
    }

    private void setRBTimeFormat() {

        if(!startAM && !startPM && !endAM && !endPM){
            rb24H.setChecked(true);
            rbAMPM.setChecked(false);
            endTime.setText(favEndHour + ":" + favEndMinute);
            startTime.setText(favStartHour + ":" + favStartMinute);
            txtStartAMPM.setVisibility(View.INVISIBLE);
            txtEndAMPM.setVisibility(View.INVISIBLE);
        } else {
            rb24H.setChecked(false);
            rbAMPM.setChecked(true);
            endTime.setText(MainActivity.changeTimeToAMPM(favEndHour + ":" + favEndMinute));
            startTime.setText(MainActivity.changeTimeToAMPM(favStartHour + ":" + favStartMinute));

            if(startAM){
                txtStartAMPM.setText("AM");
            } else {
                txtStartAMPM.setText("PM");
            }
            if(endAM){
                txtEndAMPM.setText("AM");
            } else {
                txtEndAMPM.setText("PM");
            }

            txtStartAMPM.setVisibility(View.VISIBLE);
            txtEndAMPM.setVisibility(View.VISIBLE);
        }
    }

    private void setRBDate() {
        if(rbDayFirst.getText().toString().equals(currentDateFormat)){
            dayfirst = true;
            rbDayFirst.setChecked(true);
            rbMonthFirst.setChecked(false);
        } else {
            dayfirst = false;
            rbDayFirst.setChecked(false);
            rbMonthFirst.setChecked(true);
        }
    }

    private void setOnClickListeners(){

        rb24H.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAM = false;
                startPM = false;
                endAM = false;
                endPM = false;

                rb24H.setChecked(true);
                rbAMPM.setChecked(false);

                endTime.setText(favEndHour + ":" + favEndMinute);
                startTime.setText(favStartHour + ":" + favStartMinute);

                txtStartAMPM.setVisibility(View.INVISIBLE);
                txtEndAMPM.setVisibility(View.INVISIBLE);
            }
        });
        rbAMPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(startHour > 12) {
                    startHour = startHour - 12;
                    startPM = true;
                    startAM = false;
                    txtStartAMPM.setText("PM");
                } else {
                    startAM = true;
                    startPM = false;
                    txtStartAMPM.setText("AM");
                }

                if(endHour > 12) {
                    endHour = endHour - 12;
                    endPM = true;
                    endAM = false;
                    txtEndAMPM.setText("PM");
                } else {
                    endAM = true;
                    endPM = false;
                    txtEndAMPM.setText("AM");
                }

                rb24H.setChecked(false);
                rbAMPM.setChecked(true);

                endTime.setText(endHour + ":" + favEndMinute);
                startTime.setText(startHour + ":" + favStartMinute);

                txtStartAMPM.setVisibility(View.VISIBLE);
                txtEndAMPM.setVisibility(View.VISIBLE);
            }
        });

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
                onCreateDialog(TIME_DIALOG_START);
                showDialog(TIME_DIALOG_START);
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog(TIME_DIALOG_END);
                showDialog(TIME_DIALOG_END);
            }
        });

        editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog(TIME_DIALOG_START);
                showDialog(TIME_DIALOG_START);
            }
        });

        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog(TIME_DIALOG_END);
                showDialog(TIME_DIALOG_END);
            }
        });
    }

    private boolean settingsChanged() {
        boolean ampm = false;

        if(startPM || startAM || endPM || endAM){
            ampm = true;
        }

        if( rbAMPM.isChecked() == ampm
                && rbDayFirst.isChecked() == dayfirst
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

        if(rbAMPM.isChecked()) {
            editor.putBoolean("startAM", startAM);
            editor.putBoolean("startPM", startPM);
            editor.putBoolean("endAM", endAM);
            editor.putBoolean("endPM", endPM);

            startHour = Integer.valueOf(favStartHour);
            if(startAM){
                System.out.println("startAM " + favStartHour);
            } else {
                System.out.println("startPM " + favStartHour);
                startHour = startHour - 12;
            }

            endHour = Integer.valueOf(favEndHour);
            if(endAM){
                System.out.println("endAM   " + favEndHour);
            } else {
                System.out.println("endPM   " + favEndHour);
                endHour = endHour - 12;
            }

            editor.putString("fav_start_time_hour", String.valueOf(startHour));
            editor.putString("fav_start_time_minute", favStartMinute);
            editor.putString("fav_end_time_hour", String.valueOf(endHour));
            editor.putString("fav_end_time_minute", favEndMinute);
        } else {
            editor.putBoolean("startAM", false);
            editor.putBoolean("startPM", false);
            editor.putBoolean("endAM", false);
            editor.putBoolean("endPM", false);

            editor.putString("fav_start_time_hour", startTime.getText().toString().substring(0, 2));
            editor.putString("fav_start_time_minute", startTime.getText().toString().substring(3, 5));
            editor.putString("fav_end_time_hour", endTime.getText().toString().substring(0, 2));
            editor.putString("fav_end_time_minute", endTime.getText().toString().substring(3, 5));
        }

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
                return new TimePickerDialog(this, startTimePickerListener, startHour, startMinute, !rbAMPM.isChecked());
            case TIME_DIALOG_END:
                return new TimePickerDialog(this, endTimePickerListener, endHour, endMinute, !rbAMPM.isChecked());
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
