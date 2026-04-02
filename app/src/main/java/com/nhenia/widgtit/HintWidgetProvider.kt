package com.nhenia.widgtit

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val db = AppDatabase.getDatabase(context, scope)
                val adviceList = db.adviceDao().getAllAdvice()
                val randomHint = if (adviceList.isNotEmpty()) {
                    adviceList[Random.nextInt(adviceList.size)].text
                } else {
                    "No advice available. Add some in the app!"
                }

                val views = RemoteViews(context.packageName, R.layout.hint_widget_layout)
                views.setTextViewText(R.id.hint_text, randomHint)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }

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
        }
    }
}
