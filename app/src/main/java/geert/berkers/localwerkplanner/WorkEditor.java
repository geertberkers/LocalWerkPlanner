package geert.berkers.localwerkplanner;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Geert on 11-9-2015
 */
public class WorkEditor extends ActionBarActivity {

    private Work workToEdit;
    private Button btnAddWork;
    private Button btnAddExtraWork;

    private EditText dayPicker;
    private EditText monthPicker;
    private EditText yearPicker;

    private EditText startHourPicker;
    private EditText startMinutePicker;

    private EditText endHourPicker;
    private EditText endMinutePicker;

    private String dayS;
    private String monthS;
    private String yearS;
    private String startHourS;
    private String startMinuteS;
    private String endHourS;
    private String endMinuteS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_editor_layout);

        setActionbar();
        initControls();
        setOnfocusChangedListeners();
        setWorkToEdit();
    }

    public void setActionbar() {
        setTitle("Werk toevoegen");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
    }

    public void initControls() {
        btnAddWork = (Button) findViewById(R.id.btnAddWork);
        btnAddExtraWork = (Button) findViewById(R.id.btnAddExtraWork);

        dayPicker = (EditText) findViewById(R.id.dayPicker);
        monthPicker = (EditText) findViewById(R.id.monthPicker);
        yearPicker = (EditText) findViewById(R.id.yearPicker);

        startHourPicker = (EditText) findViewById(R.id.startHourPicker);
        startMinutePicker = (EditText) findViewById(R.id.startMinutePicker);

        endHourPicker = (EditText) findViewById(R.id.endHourPicker);
        endMinutePicker = (EditText) findViewById(R.id.endMinutePicker);
    }

    public void setWorkToEdit() {
        Bundle b = getIntent().getExtras();

        try {
            workToEdit = b.getParcelable("workParcelable");
            if (workToEdit != null) {
                dayPicker.setText(workToEdit.getWorkString().substring(0, 2));
                monthPicker.setText(workToEdit.getWorkString().substring(3, 5));
                yearPicker.setText(workToEdit.getWorkString().substring(6, 10));
                startHourPicker.setText(workToEdit.getStartTime().substring(0, 2));
                startMinutePicker.setText(workToEdit.getStartTime().substring(3, 5));
                endHourPicker.setText(workToEdit.getEndTime().substring(0, 2));
                endMinutePicker.setText(workToEdit.getEndTime().substring(3, 5));

                btnAddWork.setText("Werk aanpassen!");
                btnAddExtraWork.setVisibility(View.INVISIBLE);
                setTitle("Werk aanpassen");
            }
        } catch (Exception ex) {
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

            dayPicker.setText(String.valueOf(sDay));
            monthPicker.setText(String.valueOf(sMonth));
            yearPicker.setText(String.valueOf(sYear));

            btnAddExtraWork.setVisibility(View.VISIBLE);

            Log.i("NoEditWork", "No work to edit found.");
        }
        finally {
            try {
                if(!dayPicker.getText().toString().isEmpty()) {
                    dayS = dayPicker.getText().toString();
                }
                monthS = monthPicker.getText().toString();
                yearS = yearPicker.getText().toString();
                startHourS = startHourPicker.getText().toString();
                startMinuteS = startMinutePicker.getText().toString();
                endHourS = endHourPicker.getText().toString();
                endMinuteS = endMinutePicker.getText().toString();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void setOnfocusChangedListeners() {
        dayPicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!dayPicker.getText().toString().isEmpty()) {
                    dayS = dayPicker.getText().toString();
                }
                restoreNumber();
                dayPicker.setText("");
                return false;
            }
        });
        monthPicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    monthS = monthPicker.getText().toString();
                    restoreNumber();
                    monthPicker.setText("");
                }}});
        yearPicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    yearS = yearPicker.getText().toString();
                    restoreNumber();
                    yearPicker.setText("");
                }}});
        startHourPicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    startHourS = startHourPicker.getText().toString();
                    restoreNumber();
                    startHourPicker.setText("");
                }}});
        startMinutePicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    startMinuteS = startMinutePicker.getText().toString();
                    restoreNumber();
                    startMinutePicker.setText("");
                }}});
        endHourPicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    endHourS = endHourPicker.getText().toString();
                    restoreNumber();
                    endHourPicker.setText("");
                }}});
        endMinutePicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    endMinuteS = endMinutePicker.getText().toString();
                    restoreNumber();
                    endMinutePicker.setText("");
                }}});
    }

    public void restoreNumber() {
        if(dayPicker.getText().toString().isEmpty()){
            dayPicker.setText(dayS);
        }
        if(monthPicker.getText().toString().isEmpty()){
            monthPicker.setText(monthS);
        }
        if(yearPicker.getText().toString().isEmpty()){
            yearPicker.setText(yearS);
        }
        if(startHourPicker.getText().toString().isEmpty()){
            startHourPicker.setText(startHourS);
        }
        if(startMinutePicker.getText().toString().isEmpty()){
            startMinutePicker.setText(startMinuteS);
        }
        if(endHourPicker.getText().toString().isEmpty()){
            endHourPicker.setText(endHourS);
        }
        if(endMinutePicker.getText().toString().isEmpty()){
            endMinutePicker.setText(endMinuteS);
        }
    }

    public void addWork(View view) {
        String year = yearPicker.getText().toString();
        String month = monthPicker.getText().toString();
        String day = dayPicker.getText().toString();

        if (day.length() == 1) { day = "0" + day; }
        if (month.length() == 1) { month = "0" + month; }

        String startHour = startHourPicker.getText().toString();
        String startMinute = startMinutePicker.getText().toString();

        if (startHour.length() == 1) { startHour = "0" + startHour; }
        if (startMinute.length() == 1) { startMinute = "0" + startMinute; }

        String endHour = endHourPicker.getText().toString();
        String endMinute = endMinutePicker.getText().toString();

        if (endHour.length() == 1) { endHour = "0" + endHour; }
        if (endMinute.length() == 1) { endMinute = "0" + endMinute; }

        if(oneFieldIsNull()){
            Toast.makeText(this, "Voer alle velden in!", Toast.LENGTH_LONG).show();
        }else if(!checkIfDateCorrect()){
            Toast.makeText(this, "Datum niet goed ingevoerd!", Toast.LENGTH_LONG).show();
        } else if(Integer.valueOf(startHour) > 23 || Integer.valueOf(startMinute) > 59) {
            Toast.makeText(this, "Starttijd niet goed ingevoerd!", Toast.LENGTH_LONG).show();
        } else if(Integer.valueOf(endHour) > 23 || Integer.valueOf(endMinute) > 59) {
            Toast.makeText(this, "Eindtijd niet goed ingevoerd!", Toast.LENGTH_LONG).show();
        } else if (Integer.valueOf((startHour + startMinute)) > Integer.valueOf((endHour + endHour)) ){
            Toast.makeText(this, "Starttijd kan niet na eindtijd!", Toast.LENGTH_LONG).show();
        } else {
            MySQLiteHelper db = new MySQLiteHelper(this);
            Work workInDatabase = db.getWork(day + "-" + month + "-" + year);
            Work newWork = new Work(parseDate(day, month, year), startHour + ":" + startMinute, endHour + ":" + endMinute);

            if (workToEdit == null) {
                if (workInDatabase == null) {
                    System.out.println("Work in database is null");
                    db.addWork(newWork);
                    if(view.getId() == btnAddWork.getId()) {
                        finish();
                    }
                    else if (view.getId() == btnAddExtraWork.getId()) {
                        Toast.makeText(this, "Werk toegevoegen gelukt!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Werk met deze datum bestaat al!", Toast.LENGTH_LONG).show();
                }
            }else {
                if(workInDatabase == null) {
                    System.out.println("Update work: new date");
                    db.deleteWork(workToEdit);
                    db.addWork(newWork);
                    finish();
                } else if(workInDatabase.getDate(false).equals(newWork.getDate(false))) {
                    System.out.println("Update work: same date");
                    db.updateWork(newWork);
                    finish();
                }
            }
        }
    }

    private boolean oneFieldIsNull() {
        return dayPicker.getText().toString().isEmpty() || monthPicker.getText().toString().isEmpty() || yearPicker.getText().toString().isEmpty() ||
                startHourPicker.getText().toString().isEmpty() || startMinutePicker.getText().toString().isEmpty()
                || endHourPicker.getText().toString().isEmpty() || endMinutePicker.getText().toString().isEmpty();
    }

    private boolean checkIfDateCorrect() {
        boolean correct = false;
        try{
            int day = Integer.valueOf(dayPicker.getText().toString());
            int month = Integer.valueOf(monthPicker.getText().toString());
            int year = Integer.valueOf(yearPicker.getText().toString());
            if(year > 2014){
                switch (month) {
                    case 1:
                    case 3:
                    case 7:
                    case 8:
                    case 10:
                    case 12: if(day < 32){ correct = true; } break;

                    case 4:
                    case 6:
                    case 9:
                    case 11: if(day < 31){ correct = true; } break;

                    case 2:
                        if((year%400==0 || year%100!=0) && (year%4==0)) {
                            if(day < 30){ correct = true; }
                        } else {
                            if(day < 29){ correct = true; }
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Date incorrect");
        }
        return correct;
    }

    public static Date parseDate(String day, String month, String year) {
        try {
            String date = day + "-" + month + "-" + year;
            return new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            return null;
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
}
