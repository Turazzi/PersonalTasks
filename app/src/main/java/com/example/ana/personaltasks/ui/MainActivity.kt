package com.example.ana.personaltasks.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ana.personaltasks.R
import com.example.ana.personaltasks.adapter.TaskAdapter
import com.example.ana.personaltasks.controller.MainController
import com.example.ana.personaltasks.databinding.ActivityMainBinding
import com.example.ana.personaltasks.model.Constant.EXTRA_TASK
import com.example.ana.personaltasks.model.Constant.EXTRA_VIEW_TASK
import com.example.ana.personaltasks.model.Task

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
        MainController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.subtitle = getString(R.string.task_list)

        acResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK) {

                val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(EXTRA_TASK, Task::class.java)
                }
                else {
                    result.data?.getParcelableExtra<Task>(EXTRA_TASK)
                }

                task?.let { receivedTask ->
                    val position = taskList.indexOfFirst {it.id == receivedTask.id}
                    if (position == -1) {
                        taskList.add(receivedTask)
                        taskAdapter.notifyItemChanged(position)
                        mainController.insertTask(receivedTask)
                    }
                }
            }
        }

        amb.taskRv.adapter = taskAdapter
        amb.taskRv.layoutManager = LinearLayoutManager(this)

        fillTaskList()

    }

    private fun fillTaskList() {

        taskList.clear()
        Thread {
            taskList.addAll(mainController.getTasks())
            taskAdapter.notifyDataSetChanged()
        }.start()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.add_task_mi -> {
                acResult.launch(Intent(this, TaskActivity::class.java))
                true
            }
            else -> {false}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onTaskClick(position: Int) {
        Intent(this, TaskActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            putExtra(EXTRA_VIEW_TASK, true)
            startActivity(this)
        }
    }

    override fun onRemoveTaskMenuItemClick(position: Int) {
        mainController.removeTask(taskList[position].id!!)
        taskList.removeAt(position)
        taskAdapter.notifyItemRemoved(position)
        Toast.makeText(this, "Task removida!", Toast.LENGTH_SHORT).show()
    }

    override fun onEditTaskMenuItemClick(position: Int) {
        Intent(this, TaskActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            acResult.launch(this)
        }
    }


}