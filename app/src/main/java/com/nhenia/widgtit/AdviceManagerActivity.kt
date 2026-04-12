package com.nhenia.widgtit

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdviceManagerActivity : AppCompatActivity() {

    private lateinit var adviceDao: AdviceDao
    private lateinit var adapter: AdviceAdapter

    private val CREATE_FILE_REQUEST_CODE = 1
    private val OPEN_FILE_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advice_manager)

        val db = AppDatabase.getDatabase(this, lifecycleScope)
        adviceDao = db.adviceDao()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        adapter = AdviceAdapter { advice ->
            lifecycleScope.launch {
                adviceDao.delete(advice)
                updateList()
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val editAdvice = findViewById<EditText>(R.id.edit_advice)
        val buttonAdd = findViewById<Button>(R.id.button_add)

        buttonAdd.setOnClickListener {
            val text = editAdvice.text.toString()
            if (text.isNotBlank()) {
                lifecycleScope.launch {
                    adviceDao.insert(Advice(text = text))
                    editAdvice.text.clear()
                    updateList()
                }
            }
        }

        findViewById<Button>(R.id.button_save_backup).setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, "hints_backup.txt")
            }
            startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
        }

        findViewById<Button>(R.id.button_load_backup).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
            }
            startActivityForResult(intent, OPEN_FILE_REQUEST_CODE)
        }

        findViewById<Button>(R.id.button_settings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        updateList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri = data.data!!
            if (requestCode == CREATE_FILE_REQUEST_CODE) {
                lifecycleScope.launch {
                    val hints = adviceDao.getAllAdvice()
                    val content = hints.joinToString("\n") { it.text }
                    try {
                        withContext(Dispatchers.IO) {
                            contentResolver.openOutputStream(uri)?.use { outputStream ->
                                outputStream.write(content.toByteArray())
                            }
                        }
                        Toast.makeText(this@AdviceManagerActivity, "Backup saved", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@AdviceManagerActivity, "Error saving backup", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (requestCode == OPEN_FILE_REQUEST_CODE) {
                lifecycleScope.launch {
                    try {
                        val newAdviceList = withContext(Dispatchers.IO) {
                            contentResolver.openInputStream(uri)?.use { inputStream ->
                                val reader = BufferedReader(InputStreamReader(inputStream))
                                val lines = reader.readLines()
                                lines.filter { it.isNotBlank() }.map { Advice(text = it.trim()) }
                            } ?: emptyList()
                        }
                        if (newAdviceList.isNotEmpty()) {
                            adviceDao.insertAll(newAdviceList)
                            updateList()
                            Toast.makeText(this@AdviceManagerActivity, "Backup loaded", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@AdviceManagerActivity, "Error loading backup", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateList() {
        lifecycleScope.launch {
            val allAdvice = adviceDao.getAllAdvice()
            adapter.setAdvice(allAdvice)

            // Notify widget to update
            val updateIntent = Intent(this@AdviceManagerActivity, HintWidgetProvider::class.java).apply {
                action = "com.nhenia.widgtit.UPDATE_WIDGET"
            }
            sendBroadcast(updateIntent)
        }
    }

    class AdviceAdapter(private val onDelete: (Advice) -> Unit) :
        RecyclerView.Adapter<AdviceAdapter.AdviceViewHolder>() {

        private var adviceList = emptyList<Advice>()

        class AdviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.textView)
            val deleteButton: View = itemView.findViewById(R.id.button_delete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdviceViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
            return AdviceViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: AdviceViewHolder, position: Int) {
            val current = adviceList[position]
            holder.textView.text = current.text
            holder.deleteButton.setOnClickListener { onDelete(current) }
        }

        override fun getItemCount() = adviceList.size

        fun setAdvice(advice: List<Advice>) {
            this.adviceList = advice
            notifyDataSetChanged()
        }
    }
}
