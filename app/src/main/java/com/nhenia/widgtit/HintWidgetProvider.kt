package com.nhenia.widgtit

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class HintWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            } finally {
                pendingResult?.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (Intent.ACTION_USER_PRESENT == intent.action || "com.nhenia.widgtit.UPDATE_WIDGET" == intent.action) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val thisWidget = ComponentName(context, HintWidgetProvider::class.java)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
                    for (appWidgetId in appWidgetIds) {
                        updateAppWidget(context, appWidgetManager, appWidgetId)
                    }
                } finally {
                    pendingResult?.finish()
                }
            }
        }
    }

    companion object {
        fun getRandomHint(hints: List<Advice>): String {
            if (hints.isEmpty()) return "No hints available"
            return hints[Random.nextInt(hints.size)].text
        }

        suspend fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            try {
                val db = AppDatabase.getDatabase(context, CoroutineScope(Dispatchers.IO))
                val adviceDao = db.adviceDao()
                val hints = adviceDao.getAllAdvice()

                val randomHint = getRandomHint(hints)

                val views = RemoteViews(context.packageName, R.layout.hint_widget_layout)
                views.setTextViewText(R.id.hint_text, randomHint)

                val intent = Intent(context, AdviceManagerActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
