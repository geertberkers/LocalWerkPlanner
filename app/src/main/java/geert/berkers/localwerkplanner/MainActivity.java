package geert.berkers.localwerkplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    static Context context;
    private MySQLiteHelper db;

    private ListView listView;
    private ListView workListView;
    private TextView emptyTextView;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private MenuAdapter menuAdapter;
    private ActionBarDrawerToggle drawerListener;

    private ArrayList<Work> workList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle b = getIntent().getExtras();
        try {
            boolean x = b.getBoolean("finish");
            if (x) {
                finish();
            }
        }
        catch (Exception ex){
            System.out.println("No finish");
        }
        initControls();

        db = new MySQLiteHelper(this);
        menuAdapter = new MenuAdapter(this);

        listView.setAdapter(menuAdapter);
        listView.setOnItemClickListener(this);

        mSwipeRefreshLayout.setColorSchemeColors(R.color.blauw);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWorkFromDatabase();
            }
        });

        drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                getWorkFromDatabase();
            }
        };

        drawerLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getWorkFromDatabase();
    }

    private void initControls() {
        context = this.getApplicationContext();
        listView = (ListView) findViewById(R.id.drawerList);
        workListView = (ListView) findViewById(R.id.workList);
        emptyTextView = (TextView) findViewById(R.id.emptyText);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    }

    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public void selectItem(int position) {
        listView.setItemChecked(position, true);

        if (menuAdapter.getItem(position).equals("Planning")) {
            setTitle("Werk Planner");
            showWork(false);
            drawerLayout.closeDrawer(listView);
        } else if (menuAdapter.getItem(position).equals("Gewerkt")) {
            setTitle("Gewerkt");
            showWork(true);
            drawerLayout.closeDrawer(listView);
        } else if (menuAdapter.getItem(position).equals("Info")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Info");
            alertDialog.setMessage("Simpele werk planner!\nVersie: 1.0\nOntwikkelaar: Geert Berkers");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    // DO NOTHING
                }
            });
            alertDialog.setIcon(R.drawable.ic_info_outline_black_36dp);
            alertDialog.show();
        }
        else if (menuAdapter.getItem(position).equals("Instellingen")) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
    }

    public void showWork(boolean past) {
        mSwipeRefreshLayout.setRefreshing(false);

        ArrayList<Work> tempWorkList = new ArrayList<>();

        for(Work w : workList) {
            if (w.getPastBoolean() == past) {
                tempWorkList.add(w.getWork());
            }
        }

        if(tempWorkList.isEmpty())
        {
            if(past){
                emptyTextView.setText("Nog niet gewerkt!");
            }
            else{
                emptyTextView.setText("Nog geen werk toegevoegd!");
            }
            this.emptyTextView.setVisibility(View.VISIBLE);
            this.workListView.setVisibility(View.INVISIBLE);
        } else {
            sortList();
            this.workListView.setVisibility(View.VISIBLE);
            this.emptyTextView.setVisibility(View.INVISIBLE);
            WorkAdapter workAdapter = new WorkAdapter(this.getApplicationContext(), tempWorkList, MainActivity.this);
            workListView.setAdapter(workAdapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_plus) {
            Intent workEditorIntent = new Intent(this, WorkEditor.class);
            startActivity(workEditorIntent);

            return true;
        } else if (drawerListener.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
        listView.setAdapter(menuAdapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
    }

    private void sortList() {
        Collections.sort(workList, new Comparator<Work>() {
            @Override
            public int compare(Work o1, Work o2) {
                return o1.getDate(true).compareTo(o2.getDate(true));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWorkFromDatabase();
    }

    public void getWorkFromDatabase() {
        workList = db.getAllWorks();

        if (getTitle().toString().equals("Gewerkt")) {
            showWork(true);
        } else {
            showWork(false);
        }
    }

    public static void refresh() {
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("finish", true);
        context.startActivity(i);
    }
}
