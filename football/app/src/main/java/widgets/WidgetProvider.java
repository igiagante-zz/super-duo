package widgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.igiagante.football.MainActivity;
import com.example.igiagante.football.R;

/**
 * @author igiagante on 12/2/15.
 */
public class WidgetProvider extends AppWidgetProvider {

    public static String MATCHES_WIDGET_UPDATE = "com.igiagante.widget.MATCHES_WIDGET_UPDATE";
    private static final int PERIOD = 1800000; // 30 minutes. For testing changes to 60000 (1 minute).

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetIds[]) {

        for (int appWidgetId : appWidgetIds) {

            RemoteViews footballRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_provider_layout);

            Intent footballServiceIntent = new Intent(context, WidgetService.class);
            footballServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            footballServiceIntent.setData(Uri.parse(footballServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            footballRemoteViews.setRemoteAdapter(R.id.widget_list_matches, footballServiceIntent);

            Intent templateIntent = new Intent(context, MainActivity.class);
            templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            templateIntent.setData(Uri.parse(footballServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent templatePendingIntent = PendingIntent.getActivity(context, 0,
                    templateIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            footballRemoteViews.setPendingIntentTemplate(R.id.widget_list_matches, templatePendingIntent);

            footballRemoteViews.setOnClickPendingIntent(R.id.widget_header, getLaunchIntent(context));

            appWidgetManager.updateAppWidget(appWidgetId, footballRemoteViews);
        }
    }

    private PendingIntent getLaunchIntent(Context context) {
        Intent launchIntent = new Intent(context, MainActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, 0, launchIntent, 0);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("onEnabled", context.getString(R.string.on_enabled));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), PERIOD, createClockTickIntent(context));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d("onDisabled", context.getString(R.string.on_disabled));
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
    }


    private PendingIntent createClockTickIntent(Context context) {
        Intent intent = new Intent(MATCHES_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (MATCHES_WIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));

            for (int appWidgetId : appWidgetIds) {

                RemoteViews footballRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_provider_layout);

                Intent footballServiceIntent = new Intent(context, WidgetService.class);
                footballServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                footballServiceIntent.setData(Uri.parse(footballServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));

                footballRemoteViews.setRemoteAdapter(R.id.widget_list_matches, footballServiceIntent);
                footballRemoteViews.setEmptyView(R.id.widget_list_matches, R.id.no_match_for_today);

                appWidgetManager.updateAppWidget(appWidgetId, footballRemoteViews);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_matches);
            }
        }
    }
}
