package geert.berkers.localwerkplanner;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Geert on 6-10-2015
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        System.out.println("onGetViewFactory WidgetService");
        System.out.println("Service appwidgetID: " + appWidgetId);

        return (new ListProvider(this.getApplicationContext(), intent));
    }
}