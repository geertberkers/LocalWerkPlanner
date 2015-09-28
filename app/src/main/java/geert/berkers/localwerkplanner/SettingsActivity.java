package geert.berkers.localwerkplanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Geert on 23-9-2015
 */
public class SettingsActivity extends ActionBarActivity {

    //Spinner spinner;

    RadioButton rbDayFirst;
    RadioButton rbMonthFirst;

    EditText favStartTimeHour;
    EditText favStartTimeMinute;
    EditText favEndTimeHour;
    EditText favEndTimeMinute;

    String currentDateFormat;
    String favStartHour;
    String favStartMinute;
    String favEndHour;
    String favEndMinute;
    //String theme;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        initControls();
        setOnFocusChanged();
        setSettings();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.settings);
    }

    private void setOnFocusChanged() {
        favStartTimeHour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!favStartTimeHour.getText().toString().isEmpty()) {
                    favStartHour = favStartTimeHour.getText().toString();
                }
                restoreNumber();
                favStartTimeHour.setText("");
                return false;
            }
        });
        favStartTimeMinute.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    favStartMinute = favStartTimeMinute.getText().toString();
                    restoreNumber();
                    favStartTimeMinute.setText("");
                }
            }
        });
        favEndTimeHour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    favEndHour = favEndTimeHour.getText().toString();
                    restoreNumber();
                    favEndTimeHour.setText("");
                }
            }
        });
        favEndTimeMinute.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    favEndMinute = favEndTimeMinute.getText().toString();
                    restoreNumber();
                    favEndTimeMinute.setText("");
                }
            }
        });
    }

    public void restoreNumber() {
        if(favStartTimeHour.getText().toString().isEmpty()){
            favStartTimeHour.setText(favStartHour);
        }
        if(favStartTimeMinute.getText().toString().isEmpty()){
            favStartTimeMinute.setText(favStartMinute);
        }
        if(favEndTimeHour.getText().toString().isEmpty()){
            favEndTimeHour.setText(favEndHour);
        }
        if(favEndTimeMinute.getText().toString().isEmpty()){
            favEndTimeMinute.setText(favEndMinute);
        }
    }

    private void setSettings() {
        currentDateFormat = sharedPref.getString("dateFormat", "dd-MM-yyyy");
        favStartHour = sharedPref.getString("fav_start_time_hour", "18");
        favStartMinute = sharedPref.getString("fav_start_time_minute", "00");
        favEndHour = sharedPref.getString("fav_end_time_hour", "20");
        favEndMinute = sharedPref.getString("fav_end_time_minute", "30");
        //theme = sharedPref.getString("theme", "Black");

        favStartTimeHour.setText(favStartHour);
        favStartTimeMinute.setText(favStartMinute);
        favEndTimeHour.setText(favEndHour);
        favEndTimeMinute.setText(favEndMinute);

        if(rbDayFirst.getText().toString().equals(currentDateFormat)){
            rbDayFirst.setChecked(true);
            rbMonthFirst.setChecked(false);
        } else {
            rbDayFirst.setChecked(false);
            rbMonthFirst.setChecked(true);
        }

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
        /*
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.theme));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(spinnerArrayAdapter.getPosition(theme));
        */
    }

    private void initControls() {
        //spinner = (Spinner) findViewById(R.id.spinner);
        rbDayFirst = (RadioButton) findViewById(R.id.rbDayFirst);
        rbMonthFirst = (RadioButton) findViewById(R.id.rbMonthFirst);
        favStartTimeHour = (EditText) findViewById(R.id.favStartTimeHour);
        favStartTimeMinute = (EditText) findViewById(R.id.favStartTimeMinute);
        favEndTimeHour = (EditText) findViewById(R.id.favEndTimeHour);
        favEndTimeMinute = (EditText) findViewById(R.id.favEndTimeMinute);
    }

    public void onSaveClick(View v){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putString("fav_start_time_hour", favStartTimeHour.getText().toString());
        editor.putString("fav_start_time_minute",favStartTimeMinute.getText().toString());
        editor.putString("fav_end_time_hour", favEndTimeHour.getText().toString());
        editor.putString("fav_end_time_minute", favEndTimeMinute.getText().toString());

        if(rbDayFirst.isChecked()) {
            editor.putString("dateFormat",rbDayFirst.getText().toString());
        } else {
            editor.putString("dateFormat",rbMonthFirst.getText().toString());
        }
        //editor.putString("theme", spinner.getSelectedItem().toString());
        editor.apply();

        Toast.makeText(this, R.string.setting_saved, Toast.LENGTH_LONG).show();
        finish();
    }

    public void onCancelClick(View v){
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
}
