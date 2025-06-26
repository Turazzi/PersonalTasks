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

class MainActivity : AppCompatActivity(), OnTaskClickListener {

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val taskList: MutableList<Task> = mutableListOf()
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

        // Escuta por mudanças nas tarefas em tempo real
        mainController.getTasks { tasks ->
            updateTaskList(tasks)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
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

    @SuppressLint("NotifyDataSetChanged")
    private fun updateTaskList(tasks: List<Task>) {
        val ordenatedTasks = tasks.sortedWith(compareBy {
            try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.dataLimite) ?: Date(Long.MAX_VALUE)
            } catch (e: Exception) {
                Date(Long.MAX_VALUE)
            }
        })
        taskList.clear()
        taskList.addAll(ordenatedTasks)
        taskAdapter.notifyDataSetChanged()
        updateEmptyTextView()
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

    // Agora move a tarefa para a lixeira em vez de apagar
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

    override fun onReactivateTaskMenuItemClick(position: Int) {} // Não usado aqui
}
