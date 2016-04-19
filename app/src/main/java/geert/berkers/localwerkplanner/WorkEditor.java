package geert.berkers.localwerkplanner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by Geert on 11-9-2015
 */
public class WorkEditor extends ActionBarActivity {

    private Work workToEdit;
    private Button btnAddWork;
    private Button btnAddExtraWork;
    private SharedPreferences sharedPref;
    private UpdateAppWidget updateAppWidget;

    private TextView date;
    private TextView startTime;
    private TextView endTime;

    private ImageButton editDate;
    private ImageButton editStartTime;
    private ImageButton editEndTime;

    private String startTimeS;
    private String endTimeS;

    private String dateFormat;

    private static final int TIME_DIALOG_END = 777;
    private static final int TIME_DIALOG_START = 888;
    private static final int DATE_DIALOG = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_editor_layout);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        dateFormat = sharedPref.getString("dateFormat", "dd-MM-yyyy");

        setActionbar();
        initControls();
        setWorkToEdit();
        setOnClickListeners();

        updateAppWidget = new UpdateAppWidget(this);
    }

    private void setActionbar() {
        setTitle(getString(R.string.add_job));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
    }

    private void initControls() {
        btnAddWork = (Button) findViewById(R.id.btnAddWork);
        btnAddExtraWork = (Button) findViewById(R.id.btnAddExtraWork);

        date = (TextView) findViewById(R.id.txtDatumValue);
        endTime = (TextView) findViewById(R.id.txtEndTime);
        startTime = (TextView) findViewById(R.id.txtStartTime);

        editDate = (ImageButton) findViewById(R.id.editDate);
        editEndTime = (ImageButton) findViewById(R.id.editEndTime);
        editStartTime = (ImageButton) findViewById(R.id.editStartTime);
    }

    private void setWorkToEdit() {
        Bundle b = getIntent().getExtras();

        try {
            workToEdit = b.getParcelable("workParcelable");
            if (workToEdit != null) {
                String dateString = workToEdit.getWorkString(dateFormat);

                date.setText(dateString.substring(0, 10));
                startTime.setText(dateString.substring(17, 22));
                endTime.setText(dateString.substring(23, 28));

                btnAddWork.setText(R.string.change_job);
                btnAddExtraWork.setVisibility(View.INVISIBLE);
                setTitle(R.string.change_job);
            }
        } catch (Exception ex) {
            //No work to edit found.
            Time now = new Time();
            now.setToNow();

            int year = now.year;
            int month = now.month;
            int day = now.monthDay;

            month++;

            String sDay = String.valueOf(day);
            if(day < 10) { sDay = "0" + String.valueOf(day); }

            String sMonth = String.valueOf(month);
            if(month < 10) { sMonth = "0" + String.valueOf(month); }

            String sYear = String.valueOf(year);

            switch (dateFormat){
                case "dd-MM-yyyy":
                    date.setText(sDay + "-" + sMonth + "-" + sYear);
                    break;
                case "MM-dd-yyyy":
                    date.setText(sMonth + "-" + sDay + "-" + sYear);
                    break;
            }

            endTimeS = sharedPref.getString("fav_end_time_hour", "20") + ":" + sharedPref.getString("fav_end_time_minute", "30");
            startTimeS = sharedPref.getString("fav_start_time_hour", "18") + ":" + sharedPref.getString("fav_start_time_minute", "00");

            endTime.setText(endTimeS);
            startTime.setText(startTimeS);

            btnAddExtraWork.setVisibility(View.VISIBLE);
        }
        finally {
            try {
                endTimeS = endTime.getText().toString();
                startTimeS = startTime.getText().toString();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void addWork(View view) {

        if (view.getId() == R.id.btnAddExtraWork) {

        }
        String month;
        String day;

        if (dateFormat.equals("MM-dd-yyyy")) {
            month = date.getText().toString().substring(0, 2);
            day = date.getText().toString().substring(3, 5);
        } else {
            month = date.getText().toString().substring(3, 5);
            day = date.getText().toString().substring(0, 2);
        }

        String year = date.getText().toString().substring(6, 10);

        if (day.length() == 1) {
            day = "0" + day;
        }
        if (month.length() == 1) {
            month = "0" + month;
        }

        startTimeS = startTime.getText().toString();
        endTimeS = endTime.getText().toString();

        /*
        if (Integer.valueOf((startTimeS.substring(0,2) + startTimeS.substring(3,5))) > Integer.valueOf((endTimeS.substring(0, 2) + endTimeS.substring(3,5))) && !endTimeS.contains("00:00")) {
            Toast.makeText(this, R.string.start_after_end, Toast.LENGTH_LONG).show();
        } else {
        */
        MySQLiteHelper db = new MySQLiteHelper(this);

        Work workInDatabase = db.getWork(day + "-" + month + "-" + year);
        Work newWork = new Work(MainActivity.parseDate(day + "-" + month + "-" + year), startTimeS, endTimeS);

        if (workToEdit == null) {
            if (workInDatabase == null) {
                db.addWork(newWork);
                if (view.getId() == btnAddWork.getId()) {
                    updateAppWidget.updateAppWidget();
                    finish();
                } else if (view.getId() == btnAddExtraWork.getId()) {
                    updateAppWidget.updateAppWidget();
                    Toast.makeText(this, R.string.work_added, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, R.string.date_exists, Toast.LENGTH_LONG).show();
            }
        } else {
            if (workInDatabase == null) {
                // Update work: new date
                db.deleteWork(workToEdit);
                db.addWork(newWork);
                updateAppWidget.updateAppWidget();
                finish();
            } else if (workInDatabase.getDate(false).equals(newWork.getDate(false))) {
                // Update work: same date
                db.updateWork(newWork);
                updateAppWidget.updateAppWidget();
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setOnClickListeners(){

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
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

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        int startHour;
        int startMinute;
        int endHour;
        int endMinute;

        try { startHour = Integer.valueOf(startTimeS.substring(0,2)); }
        catch(Exception ex) { startHour = Integer.valueOf(startTimeS.substring(0,1)); }

        try { startMinute = Integer.valueOf(startTimeS.substring(3,5)); }
        catch(Exception ex) { startMinute = Integer.valueOf(startTimeS.substring(3,4)); }

        try { endHour = Integer.valueOf(endTimeS.substring(0,2)); }
        catch(Exception ex) { endHour = Integer.valueOf(endTimeS.substring(0,1)); }

        try { endMinute = Integer.valueOf(endTimeS.substring(3,5)); }
        catch(Exception ex) { endMinute = Integer.valueOf(endTimeS.substring(3,4)); }

        String month;
        String day;

        if(dateFormat.equals("MM-dd-yyyy")) {
            month = date.getText().toString().substring(0,2);
            day = date.getText().toString().substring(3,5);
        } else {
            month = date.getText().toString().substring(3, 5);
            day = date.getText().toString().substring(0, 2);
        }

        String year = date.getText().toString().substring(6, 10);

        int dayInt;
        int monthInt;
        int yearInt = Integer.valueOf(year);

        try { dayInt = Integer.valueOf(day); }
        catch(Exception ex) { dayInt = Integer.valueOf(day.substring(0,1)); }

        try { monthInt = Integer.valueOf(month); }
        catch(Exception ex) { monthInt = Integer.valueOf(month.substring(0,1)); }

        switch (id) {
            case TIME_DIALOG_START:
                return new TimePickerDialog(this, startTimePickerListener, startHour, startMinute, true);
            case TIME_DIALOG_END:
                return new TimePickerDialog(this, endTimePickerListener, endHour, endMinute, true);
            case DATE_DIALOG:
                return new DatePickerDialog(this, datePickerListener, yearInt, (monthInt-1), dayInt);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener startTimePickerListener =  new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            String favStartHour;
            String favStartMinute;

            if(selectedHour < 10){
                favStartHour = "0" + selectedHour;
            } else { favStartHour = String.valueOf(selectedHour); }
            if(selectedMinute < 10) {
                favStartMinute = "0"+ selectedMinute;
            } else { favStartMinute = String.valueOf(selectedMinute); }
            startTimeS = favStartHour + ":" + favStartMinute;
            startTime.setText(startTimeS);
            removeDialog(TIME_DIALOG_START);
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimePickerListener =  new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            String favEndHour;
            String favEndMinute;

            if(selectedHour < 10){
                favEndHour = "0" + selectedHour;
            } else { favEndHour = String.valueOf(selectedHour); }

            if(selectedMinute < 10) {
                favEndMinute = "0"+ selectedMinute;
            } else { favEndMinute = String.valueOf(selectedMinute); }

            endTimeS = favEndHour + ":" + favEndMinute;
            endTime.setText(endTimeS);
            removeDialog(TIME_DIALOG_END);
        }
    };

    private final DatePickerDialog.OnDateSetListener datePickerListener =  new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear++;
            String month;
            String day;

            if(monthOfYear < 10){
                month = "0" + monthOfYear;
            } else { month = String.valueOf(monthOfYear); }

            if(dayOfMonth < 10) {
                day = "0"+ dayOfMonth;
            } else { day = String.valueOf(dayOfMonth); }


            switch (dateFormat){
                case "MM-dd-yyyy":
                    date.setText(new StringBuilder().append(month).append("-").append(day).append("-").append(year));
                    break;
                case "dd-MM-yyyy":
                    date.setText(new StringBuilder().append(day).append("-").append(month).append("-").append(year));
                    break;
            }
            removeDialog(DATE_DIALOG);
        }
    };
}
