package geert.berkers.localwerkplanner;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;


/**
 * Created by Geert on 6-10-2015
 */
public class WidgetProvider extends AppWidgetProvider {

    private static String UPDATE_ACTION = "UPDATE_ACTION";

    /**
     * this method is called every 'x' mins as specified on widget_info.xml
     * this method is also called on every phone reboot
     **/
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        System.out.println("OnUpdate WorkProvider");
        for (int appWidgetId : appWidgetIds) {

            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);

            Intent launchActivity = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
            remoteViews.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

            launchActivity = new Intent(context, WorkEditor.class);
            pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
            remoteViews.setOnClickPendingIntent(R.id.addWorkWidget, pendingIntent);

            launchActivity = new Intent(context, WorkEditor.class);
            pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
            remoteViews.setOnClickPendingIntent(R.id.empty_view, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(UPDATE_ACTION)) {
            System.out.println("OWN UPDATE ACTION");

            int[] appWidgets = intent.getIntArrayExtra("WidgetIDs");
            for (int appWidget : appWidgets) {

                System.out.println("Appwidget: " + appWidget);
                Intent serviceIntent = new Intent(context, WidgetProvider.class);
                serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidget);
                serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                remoteViews.setRemoteAdapter(appWidget, R.id.listViewWidget, serviceIntent);
                remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidget, R.id.listViewWidget);
                System.out.println("Widget with ID: " + appWidget + " is updated");
            }
        } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            System.out.println("APP WIDGET UPDATE");

            // UPDATE REMOTE VIEW
        } else {
            System.out.println("OTHER ACTION: " + intent.getAction());
        }
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        System.out.println("Update WidgetListView");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_layout);

        //RemoteViews Service needed to provide adapter for ListView
        //passing app widget id to that RemoteViews Service
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        //setting a unique Uri to the intent
        //don't know its purpose to me right now
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        //setting adapter to listview of the widget
        //setting an empty view in case of no data
        remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget, svcIntent);
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);

        System.out.println("Return remoteView");

        return remoteViews;
    }
}