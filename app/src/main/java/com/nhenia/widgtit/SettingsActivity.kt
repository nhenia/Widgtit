package com.nhenia.widgtit

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("widget_settings", Context.MODE_PRIVATE)

        val spinnerTheme = findViewById<Spinner>(R.id.spinner_theme)
        val spinnerFont = findViewById<Spinner>(R.id.spinner_font)
        val seekbarFontSize = findViewById<SeekBar>(R.id.seekbar_font_size)
        val spinnerBorder = findViewById<Spinner>(R.id.spinner_border)
        val spinnerBackground = findViewById<Spinner>(R.id.spinner_background)
        val buttonSave = findViewById<Button>(R.id.button_save_settings)

        // Setup Spinners
        spinnerTheme.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ThemeConstants.THEMES.map { it.name })
        spinnerFont.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ThemeConstants.FONTS)
        spinnerBorder.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Small", "Medium", "Large"))
        spinnerBackground.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ThemeConstants.BACKGROUND_IMAGES)

        // Load current values
        spinnerTheme.setSelection(prefs.getInt("theme_index", 0))
        spinnerFont.setSelection(ThemeConstants.FONTS.indexOf(prefs.getString("font_family", "Default")))
        seekbarFontSize.progress = prefs.getInt("font_size", 24)
        spinnerBorder.setSelection(prefs.getInt("border_index", 0))
        spinnerBackground.setSelection(prefs.getInt("bg_index", 0))

        buttonSave.setOnClickListener {
            prefs.edit().apply {
                putInt("theme_index", spinnerTheme.selectedItemPosition)
                putString("font_family", spinnerFont.selectedItem as String)
                putInt("font_size", seekbarFontSize.progress)
                putInt("border_index", spinnerBorder.selectedItemPosition)
                putInt("bg_index", spinnerBackground.selectedItemPosition)
                apply()
            }

            // Trigger widget update
            val intent = Intent(this, HintWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, HintWidgetProvider::class.java))
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            sendBroadcast(intent)

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
