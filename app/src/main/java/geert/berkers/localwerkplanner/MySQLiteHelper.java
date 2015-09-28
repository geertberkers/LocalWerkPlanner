package geert.berkers.localwerkplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Geert on 23-9-2015
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "WorkDB";

    // Work table name
    private static final String TABLE_WORK = "Work";

    // Work Table Columns names
    private static final String DATE = "date";
    private static final String STARTTIME = "startTime";
    private static final String ENDTIME = "endTime";

    private static final String[] COLUMNS = {DATE, STARTTIME, ENDTIME};

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create work table
        String CREATE_WORK_TABLE = "CREATE TABLE Work ( " + "date VARCHAR(10) PRIMARY KEY, " + "startTime VARCHAR(5), " +  "endTime VARCHAR(5) )";

        // create Work table
        db.execSQL(CREATE_WORK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older works table if existed
        db.execSQL("DROP TABLE IF EXISTS Work");

        // create fresh works table
        this.onCreate(db);
    }

    public void addWork(Work work){
        //for logging
        Log.d("addWork", work.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(DATE, work.getDate(false));      // get date
        values.put(STARTTIME, work.getStartTime()); // get startTime
        values.put(ENDTIME, work.getEndTime());     // get endTime

        // 3. insert
        //db.insert(table, nullColumnHack, key/value -> keys = column names/ values = column values
        try {
            db.insert(TABLE_WORK, null, values);
        } catch (Exception ex) {
            Log.e("Error 1", ex.toString());
        }
        // 4. close
        db.close();
    }

    public Work getWork(String date){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        //Cursor cursor = db.query(table, column names, selections, selections args, group by, having, order by, limit);
        Cursor cursor = db.query(TABLE_WORK, COLUMNS, " date = ?", new String[] { date }, null, null, null, null);

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build work object
        Work work = null;
        try {
            if (cursor != null) {
                work = new Work(MainActivity.parseDate(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
                Log.d("getWork(" + date + ")", work.toString());
            }
        }
        catch (Exception ex)
        {
            Log.d("getWork(" + date + ")", "Failed method");
        }

        // 5. return work
        return work;
    }

    public ArrayList<Work> getAllWorks() {
        ArrayList<Work> workList = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_WORK;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build work and add it to list
        Work work;
        if (cursor.moveToFirst()) {
            do {
                work = new Work(MainActivity.parseDate(cursor.getString(0)),cursor.getString(1),cursor.getString(2));

                // Add work to workList
                workList.add(work);
            } while (cursor.moveToNext());
        }

        Log.d("getAllWork()", workList.toString());

        // return works
        return workList;
    }


    public int updateWork(Work work) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("DATE", work.getDate(false));
        values.put("STARTTIME", work.getStartTime());   // get startTime
        values.put("ENDTIME", work.getEndTime());    // get getEndtime

        // 3. updating row
        //db.update(table,column/value,selections,selection)
        int i = db.update(TABLE_WORK, values, DATE + " = ?", new String[] { String.valueOf(work.getDate(false)) });

        // 4. close
        db.close();

        return i;

    }

    public void deleteWork(Work work) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        //db.delete(table name, selections, selections args);
        db.delete(TABLE_WORK, DATE+" = ?", new String[] { String.valueOf(work.getDate(false)) });

        // 3. close
        db.close();

        //log
        Log.d("deleteWork", work.toString());

    }
}