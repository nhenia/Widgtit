package com.nhenia.widgtit

import android.appwidget.AppWidgetManager
import android.app.PendingIntent
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.TypedValue
import android.widget.RemoteViews
import kotlin.random.Random

class HintWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (Intent.ACTION_USER_PRESENT == intent.action) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, HintWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    companion object {
        fun getRandomHint(hints: Array<String>): String {
            return hints[Random.nextInt(hints.size)]
        }

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val hints = context.resources.getStringArray(R.array.hints)
            val randomHint = getRandomHint(hints)

            val views = RemoteViews(context.packageName, R.layout.hint_widget_layout)
            views.setTextViewText(R.id.hint_text, randomHint)
            views.setContentDescription(R.id.hint_text, context.getString(R.string.widget_content_description, randomHint))

            val intent = Intent(context, HintWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Note: Auto-sizing text in RemoteViews is available since Android 8.0 (Oreo, API 26)
            // For older versions, it might not work perfectly without a custom solution.
            // But since our minSdk is 24, we should check if we can use it.
            // Actually, RemoteViews has setTextViewTextSize and some other methods.
            // RemoteViews.setRemoteAdapter can be used for collections.
            // For simple TextView, we can use views.setTextViewTextSize(id, unit, size)
            // But automatic resizing (autosize) is a feature of TextView itself.

            // Since we can't easily measure text in RemoteViews,
            // the best way to support "automatically resized" in a widget is using
            // the autosize features if they are supported in RemoteViews.
            // Android 8.0+ supports autosize for TextView.

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
