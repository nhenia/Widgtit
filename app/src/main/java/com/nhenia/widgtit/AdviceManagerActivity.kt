package com.nhenia.widgtit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class AdviceManagerActivity : AppCompatActivity() {

    private lateinit var adviceDao: AdviceDao
    private lateinit var adapter: AdviceAdapter

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

        updateList()
    }

    private fun updateList() {
        lifecycleScope.launch {
            val allAdvice = adviceDao.getAllAdvice()
            adapter.setAdvice(allAdvice)
        }
    }

    class AdviceAdapter(private val onDelete: (Advice) -> Unit) :
        RecyclerView.Adapter<AdviceAdapter.AdviceViewHolder>() {

        private var adviceList = emptyList<Advice>()

        class AdviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.textView)
            val deleteButton: Button = itemView.findViewById(R.id.button_delete)
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
