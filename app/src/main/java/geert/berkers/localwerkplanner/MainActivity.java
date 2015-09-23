package geert.berkers.localwerkplanner;

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
        } else if (menuAdapter.getItem(position).equals("Instellingen")) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
    }

    public void showWork(boolean past) {
        mSwipeRefreshLayout.setRefreshing(false);

        if(workList.isEmpty())
        {
            this.emptyTextView.setVisibility(View.VISIBLE);
            this.workListView.setVisibility(View.INVISIBLE);
        } else {
            sortList();
            this.workListView.setVisibility(View.VISIBLE);
            this.emptyTextView.setVisibility(View.INVISIBLE);
            WorkAdapter workAdapter = new WorkAdapter(this.getApplicationContext(), workList, past, MainActivity.this);
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
            workEditorIntent.putExtra("workList", workList);
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
}
