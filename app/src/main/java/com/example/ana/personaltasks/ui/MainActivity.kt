package com.example.ana.personaltasks.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ana.personaltasks.R
import com.example.ana.personaltasks.adapter.TaskAdapter
import com.example.ana.personaltasks.controller.MainController
import com.example.ana.personaltasks.databinding.ActivityMainBinding
import com.example.ana.personaltasks.model.Constant
import com.example.ana.personaltasks.model.Task
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference

class MainActivity : AppCompatActivity(), OnTaskClickListener, SearchView.OnQueryTextListener {

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var taskList: MutableList<Task> = mutableListOf()
    private val allTasks: MutableList<Task> = mutableListOf()
    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(taskList, this)
    }
    private lateinit var acResult: ActivityResultLauncher<Intent>
    private val mainController: MainController by lazy {
        MainController()
    }
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    // Para guardar o texto da busca atual
    private var currentSearchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        findViewById<ImageView>(R.id.toolbar_icon).setOnClickListener {
            acResult.launch(Intent(this, TaskActivity::class.java))
        }

        acResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(Constant.EXTRA_TASK, Task::class.java)
                } else {
                    result.data?.getParcelableExtra<Task>(Constant.EXTRA_TASK)
                }
                task?.let { receivedTask ->
                    val isNewTask = taskList.find { it.id == receivedTask.id } == null
                    if (isNewTask) {
                        mainController.insertTask(receivedTask)
                    } else {
                        mainController.modifyTask(receivedTask)
                    }
                }
            }
        }

        amb.taskRv.adapter = taskAdapter
        amb.taskRv.layoutManager = LinearLayoutManager(this)

        mainController.getTasks { tasks ->
            val sortedTasks = tasks.sortedWith(compareBy {
                try {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.dataLimite) ?: Date(Long.MAX_VALUE)
                } catch (e: Exception) {
                    Date(Long.MAX_VALUE)
                }
            })
            allTasks.clear()
            allTasks.addAll(sortedTasks)
            // Filtra a lista com a busca atual (que pode ser nula)
            filterTasks(currentSearchQuery)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        currentSearchQuery = newText
        filterTasks(newText)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterTasks(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            allTasks
        } else {
            allTasks.filter {
                it.titulo.contains(query, ignoreCase = true)
            }
        }
        taskList.clear()
        taskList.addAll(filteredList)
        taskAdapter.notifyDataSetChanged()
        updateEmptyTextView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleted_tasks_mi -> {
                startActivity(Intent(this, DeletedTasksActivity::class.java))
                true
            }
            R.id.logout_mi -> {
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateEmptyTextView() {
        amb.emptyTv.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onTaskClick(position: Int) {
        val intent = Intent(this, TaskActivity::class.java).apply {
            putExtra(Constant.EXTRA_TASK, taskList[position])
            putExtra(Constant.EXTRA_VIEW_TASK, true)
        }
        startActivity(intent)
    }

    override fun onRemoveTaskMenuItemClick(position: Int) {
        val task = taskList[position]
        mainController.removeTask(task)
        Toast.makeText(this, "Tarefa movida para a lixeira!", Toast.LENGTH_SHORT).show()
    }

    override fun onEditTaskMenuItemClick(position: Int) {
        val intent = Intent(this, TaskActivity::class.java).apply {
            putExtra(Constant.EXTRA_TASK, taskList[position])
        }
        acResult.launch(intent)
    }

    override fun onTaskCheckClick(position: Int, isChecked: Boolean) {
        val task = taskList[position]
        task.concluida = isChecked
        mainController.modifyTask(task)
    }

    override fun onReactivateTaskMenuItemClick(position: Int) {}
}