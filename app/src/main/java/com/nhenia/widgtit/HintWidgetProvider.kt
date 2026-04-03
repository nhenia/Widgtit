package com.nhenia.widgtit

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlin.random.Random

class HintWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAppWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (Intent.ACTION_USER_PRESENT == intent.action) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, HintWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            updateAppWidgets(context, appWidgetManager, appWidgetIds)
        }
    }

    companion object {
        private var hintsCache: Array<String>? = null

        private fun getHints(context: Context): Array<String> {
            return hintsCache ?: context.resources.getStringArray(R.array.hints).also {
                hintsCache = it
            }
        }

        fun getRandomHint(hints: Array<String>): String {
            return hints[Random.nextInt(hints.size)]
        }

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            updateAppWidgets(context, appWidgetManager, intArrayOf(appWidgetId))
        }

        fun updateAppWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            if (appWidgetIds.isEmpty()) return

            val hints = getHints(context)
            val randomHint = getRandomHint(hints)

            val views = RemoteViews(context.packageName, R.layout.hint_widget_layout)
            views.setTextViewText(R.id.hint_text, randomHint)

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

            appWidgetManager.updateAppWidget(appWidgetIds, views)
        }
    }
}
