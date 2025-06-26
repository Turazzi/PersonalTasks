package com.example.ana.personaltasks.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ana.personaltasks.R
import com.example.ana.personaltasks.adapter.TaskAdapter
import com.example.ana.personaltasks.controller.MainController
import com.example.ana.personaltasks.databinding.ActivityDeletedTasksBinding
import com.example.ana.personaltasks.model.Constant
import com.example.ana.personaltasks.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DeletedTasksActivity: AppCompatActivity(), OnTaskClickListener, SearchView.OnQueryTextListener {

    private val binding: ActivityDeletedTasksBinding by lazy {
        ActivityDeletedTasksBinding.inflate(layoutInflater)
    }
    private val deletedTaskList: MutableList<Task> = mutableListOf()
    private val allDeletedTasks: MutableList<Task> = mutableListOf()
    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(deletedTaskList, this, isDeletedList = true)
    }
    private val mainController: MainController by lazy {
        MainController()
    }
    private var currentSearchQuery: String? = null
    // Variável para filtro de data única
    private var selectedDateFilter: String? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarIn.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarIn.toolbar.findViewById<ImageView>(R.id.toolbar_icon).visibility = View.GONE

        binding.deletedTasksRv.layoutManager = LinearLayoutManager(this)
        binding.deletedTasksRv.adapter = taskAdapter

        mainController.getDeletedTasks { tasks ->
            val sortedTasks = tasks.sortedWith(compareBy {
                try {
                    dateFormat.parse(it.dataLimite) ?: Date(Long.MAX_VALUE)
                } catch (e: Exception) {
                    Date(Long.MAX_VALUE)
                }
            })
            allDeletedTasks.clear()
            allDeletedTasks.addAll(sortedTasks)
            filterDeletedTasks()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.deleted_tasks_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search_deleted)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)

        searchView?.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                selectedDateFilter = null
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter_by_date_deleted -> {
                showDatePickerDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, y, m, d ->
            calendar.set(y, m, d)
            selectedDateFilter = dateFormat.format(calendar.time)
            currentSearchQuery = null
            filterDeletedTasks()
        }, year, month, day)

        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Limpar filtro") { _, _ ->
            selectedDateFilter = null
            filterDeletedTasks()
        }
        datePickerDialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        currentSearchQuery = newText
        filterDeletedTasks()
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterDeletedTasks() {
        val listToShow = when {
            selectedDateFilter != null -> {
                allDeletedTasks.filter { it.dataLimite == selectedDateFilter }
            }
            !currentSearchQuery.isNullOrBlank() -> {
                allDeletedTasks.filter {
                    it.titulo.contains(currentSearchQuery!!, ignoreCase = true) ||
                            it.descricao.contains(currentSearchQuery!!, ignoreCase = true)
                }
            }
            else -> allDeletedTasks
        }
        deletedTaskList.clear()
        deletedTaskList.addAll(listToShow)
        taskAdapter.notifyDataSetChanged()
        binding.emptyDeletedTv.visibility = if (deletedTaskList.isEmpty()) View.VISIBLE else View.GONE
    }


    override fun onTaskClick(position: Int) {
        val intent = Intent(this, TaskActivity::class.java).apply {
            putExtra(Constant.EXTRA_TASK, deletedTaskList[position])
            putExtra(Constant.EXTRA_VIEW_TASK, true)
        }
        startActivity(intent)
    }

    override fun onReactivateTaskMenuItemClick(position: Int) {
        val task = deletedTaskList[position]
        mainController.reactivateTask(task)
        Toast.makeText(this, "Tarefa reativada!", Toast.LENGTH_SHORT).show()
    }

    override fun onRemoveTaskMenuItemClick(position: Int) {}
    override fun onEditTaskMenuItemClick(position: Int) {}
    override fun onTaskCheckClick(position: Int, isChecked: Boolean) {}
}