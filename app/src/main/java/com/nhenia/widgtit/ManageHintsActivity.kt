package com.nhenia.widgtit

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nhenia.widgtit.data.AppDatabase
import com.nhenia.widgtit.data.Hint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManageHintsActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: ArrayAdapter<String>
    private val hintList = mutableListOf<Hint>()
    private val hintStrings = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_hints)

        db = AppDatabase.getDatabase(this)

        val listView = findViewById<ListView>(R.id.hints_list)
        val input = findViewById<EditText>(R.id.hint_input)
        val addButton = findViewById<Button>(R.id.add_hint_button)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, hintStrings)
        listView.adapter = adapter

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val hintToDelete = hintList[position]
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.hintDao().delete(hintToDelete)
                }
                refreshHints()
                Toast.makeText(this@ManageHintsActivity, "Hint deleted", Toast.LENGTH_SHORT).show()
            }
            true
        }

        addButton.setOnClickListener {
            val text = input.text.toString()
            if (text.isNotBlank()) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.hintDao().insert(Hint(text = text))
                    }
                    input.text.clear()
                    refreshHints()
                    Toast.makeText(this@ManageHintsActivity, "Hint added", Toast.LENGTH_SHORT).show()
                }
            }
        }

        refreshHints()
    }

    private fun refreshHints() {
        lifecycleScope.launch {
            val hintsFromDb = withContext(Dispatchers.IO) {
                db.hintDao().getAll()
            }

            // If the database is empty, prepopulate it with default hints
            if (hintsFromDb.isEmpty()) {
                val defaultHints = resources.getStringArray(R.array.hints)
                withContext(Dispatchers.IO) {
                    for (h in defaultHints) {
                        db.hintDao().insert(Hint(text = h))
                    }
                }
                refreshHints() // Recursive call to load the now-populated hints
                return@launch
            }

            hintList.clear()
            hintList.addAll(hintsFromDb)
            hintStrings.clear()
            hintStrings.addAll(hintsFromDb.map { it.text })
            adapter.notifyDataSetChanged()
        }
    }
}
