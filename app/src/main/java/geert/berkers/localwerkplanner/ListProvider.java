package geert.berkers.localwerkplanner;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Geert on 6-10-2015
 */
public class ListProvider implements RemoteViewsFactory {

    private ArrayList<Work> workList = new ArrayList<>();
    private String currentDateFormat;
    private Context context = null;

    public ListProvider(Context context, Intent intent) {
        this.context = context;

        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        System.out.println("Listprovider appWidgetID: " + appWidgetId);

        getWorkForWidget();
    }

    private void getWorkForWidget(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        currentDateFormat = sharedPref.getString("dateFormat", "dd-MM-yyyy");

        workList = new ArrayList<>();
        MySQLiteHelper db = new MySQLiteHelper(context);

        // Add all jobs in future
        for(Work w : db.getAllWorks()){
            if(!w.getPastBoolean()){
                workList.add(w);
            }
        }
        sortList(workList);
    }
    private void sortList(ArrayList<Work> workList) {
        Collections.sort(workList, new Comparator<Work>() {
            @Override
            public int compare(Work o1, Work o2) {
                return o1.getDate(true).compareTo(o2.getDate(true));
            }
        });
    }

    /*
    * Similar to getView of Adapter where instead of View
    * Return RemoteViews
    */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_row);
        Work work = workList.get(position);

        remoteView.setTextViewText(R.id.dayOfWeek, work.getDayOfWeek());
        remoteView.setTextViewText(R.id.workItem, work.getWorkString(currentDateFormat));

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        getWorkForWidget();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return workList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}