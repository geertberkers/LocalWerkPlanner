package geert.berkers.localwerkplanner;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Geert on 11-9-2015
 */
public class WorkEditor extends ActionBarActivity {

    private Work workToEdit;
    private Button btnAddWork;

    private EditText dayPicker;
    private EditText monthPicker;
    private EditText yearPicker;

    private EditText startHourPicker;
    private EditText startMinutePicker;

    private EditText endHourPicker;
    private EditText endMinutePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_editor_layout_txt);

        initControls();

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
                System.out.println("WorkToEdit added");
                }
            } catch (Exception ex) {
                Log.e("Error 2", ex.toString());
            }

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        }

    public void initControls() {
        dayPicker = (EditText) findViewById(R.id.dayPicker);
        monthPicker = (EditText) findViewById(R.id.monthPicker);
        yearPicker = (EditText) findViewById(R.id.yearPicker);

        startHourPicker = (EditText) findViewById(R.id.startHourPicker);
        startMinutePicker = (EditText) findViewById(R.id.startMinutePicker);

        endHourPicker = (EditText) findViewById(R.id.endHourPicker);
        endMinutePicker = (EditText) findViewById(R.id.endMinutePicker);

        btnAddWork = (Button) findViewById(R.id.btnAddWork);

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

    }

    public void addWork(View view)
    {
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

        Work work = new Work(parseDate(day, month, year), startHour + ":" + startMinute, endHour + ":" + endMinute  );

        MySQLiteHelper db = new MySQLiteHelper(this);

        if(workToEdit != null) {
            db.deleteWork(workToEdit);
        }

        db.addWork(work);

        finish();
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
