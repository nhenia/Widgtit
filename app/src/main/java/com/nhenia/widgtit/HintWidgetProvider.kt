package com.nhenia.widgtit

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
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
                updateAppWidget(context, appWidgetManager, appWidgetIds)
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
                    updateAppWidget(context, appWidgetManager, appWidgetIds)
                } finally {
                    pendingResult?.finish()
                }
            }
        }
    }

    companion object {
        var cachedStringArrays: Array<String>? = null

        fun getRandomHint(hints: List<Advice>): String {
            if (hints.isEmpty()) return "No hints available"
            return hints[Random.nextInt(hints.size)].text
        }

        suspend fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            try {
                if (cachedStringArrays == null) {
                    cachedStringArrays = context.resources.getStringArray(R.array.hints)
                }
                val db = AppDatabase.getDatabase(context, CoroutineScope(Dispatchers.IO))
                val adviceDao = db.adviceDao()
                val hints = adviceDao.getAllAdvice()

                val randomHint = getRandomHint(hints)


                val views = RemoteViews(context.packageName, R.layout.hint_widget_layout)

                // Apply Settings
                val prefs = context.getSharedPreferences("widget_settings", Context.MODE_PRIVATE)
                val themeIndex = prefs.getInt("theme_index", 0)
                val theme = ThemeConstants.THEMES.getOrElse(themeIndex) { ThemeConstants.THEMES[0] }
                val fontSize = prefs.getInt("font_size", 24).toFloat()
                val fontFamily = prefs.getString("font_family", "Default")
                val borderIndex = prefs.getInt("border_index", 0)
                val bgIndex = prefs.getInt("bg_index", 0)

                views.setTextViewText(R.id.hint_text, randomHint)
                views.setTextColor(R.id.hint_text, theme.textColor)

                // Set background color
                views.setInt(R.id.widget_bg, "setBackgroundColor", theme.backgroundColor)

                // Set Font Size
                views.setFloat(R.id.hint_text, "setTextSize", fontSize)

                // Set Border
                val borderRes = when(borderIndex) {
                    1 -> R.drawable.border_m
                    2 -> R.drawable.border_l
                    else -> R.drawable.border_s
                }
                views.setImageViewResource(R.id.widget_border, borderRes)
                views.setInt(R.id.widget_border, "setColorFilter", theme.borderColor)

                // Set Background Pattern
                val patternRes = when(bgIndex) {
                    1 -> R.drawable.pattern_dots_tile
                    2 -> R.drawable.pattern_lines_tile
                    3 -> R.drawable.pattern_grid_tile
                    4 -> R.drawable.pattern_stars_tile
                    else -> 0
                }
                if (patternRes != 0) {
                    views.setViewVisibility(R.id.widget_pattern, View.VISIBLE)
                    views.setImageViewResource(R.id.widget_pattern, patternRes)
                    views.setInt(R.id.widget_pattern, "setColorFilter", theme.textColor)
                    views.setInt(R.id.widget_pattern, "setImageAlpha", 40) // ~15% opacity
                } else {
                    views.setViewVisibility(R.id.widget_pattern, View.GONE)
                }

                // Font Family (Best effort for RemoteViews)
                val spannable = SpannableString(randomHint)
                val typeface = when (fontFamily) {
                    "Serif" -> "serif"
                    "Monospace" -> "monospace"
                    "Sans-Serif" -> "sans-serif"
                    "sans-serif-light" -> "sans-serif-light"
                    "sans-serif-condensed" -> "sans-serif-condensed"
                    "sans-serif-medium" -> "sans-serif-medium"
                    "sans-serif-black" -> "sans-serif-black"
                    else -> "sans-serif"
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    spannable.setSpan(
                        android.text.style.TypefaceSpan(typeface),
                        0,
                        spannable.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    spannable.setSpan(
                        android.text.style.TypefaceSpan(typeface),
                        0,
                        spannable.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                views.setTextViewText(R.id.hint_text, spannable)

                val intent = Intent(context, AdviceManagerActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetIds, views)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
