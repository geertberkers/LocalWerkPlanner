package geert.berkers.localwerkplanner;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Geert on 7-10-2015
 */
public class UpdateAppWidget {

    private final Context context;

    public  UpdateAppWidget(Context context){
        this.context = context;
    }

    public void updateAppWidget() {
        ComponentName appWidget = new ComponentName(context, WidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);

        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction("UPDATE_ACTION");
        intent.putExtra("WidgetIDs", appWidgetIds);

        context.sendBroadcast(intent);
    }

}
