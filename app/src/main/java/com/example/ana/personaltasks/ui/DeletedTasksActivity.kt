package com.example.ana.personaltasks.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ana.personaltasks.R
import com.example.ana.personaltasks.adapter.TaskAdapter
import com.example.ana.personaltasks.controller.MainController
import com.example.ana.personaltasks.databinding.ActivityDeletedTasksBinding
import com.example.ana.personaltasks.model.Constant
import com.example.ana.personaltasks.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeletedTasksActivity: AppCompatActivity(), OnTaskClickListener {

    private val binding: ActivityDeletedTasksBinding by lazy {
        ActivityDeletedTasksBinding.inflate(layoutInflater)
    }
    private val deletedTaskList: MutableList<Task> = mutableListOf()
    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(deletedTaskList, this, isDeletedList = true)
    }
    private val mainController: MainController by lazy {
        MainController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Configuração da Toolbar
        setSupportActionBar(binding.toolbarIn.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Esconde o ícone de adicionar tarefa
        binding.toolbarIn.toolbar.findViewById<ImageView>(R.id.toolbar_icon).visibility = View.GONE

        binding.deletedTasksRv.layoutManager = LinearLayoutManager(this)
        binding.deletedTasksRv.adapter = taskAdapter

        mainController.getDeletedTasks { tasks ->
            updateDeletedTaskList(tasks)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateDeletedTaskList(tasks: List<Task>) {
        val ordenatedTasks = tasks.sortedWith(compareBy {
            try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.dataLimite) ?: Date(
                    Long.MAX_VALUE
                )
            } catch (e: Exception) {
                Date(Long.MAX_VALUE)
            }
        })
        deletedTaskList.clear()
        deletedTaskList.addAll(ordenatedTasks)
        taskAdapter.notifyDataSetChanged()
        binding.emptyDeletedTv.visibility =
            if (deletedTaskList.isEmpty()) View.VISIBLE else View.GONE
    }

    // Ação do clique para ver detalhes.
    override fun onTaskClick(position: Int) {
        val intent = Intent(this, TaskActivity::class.java).apply {
            putExtra(Constant.EXTRA_TASK, deletedTaskList[position])
            putExtra(Constant.EXTRA_VIEW_TASK, true)
        }
        startActivity(intent)
    }

    // Ação do clique para reativar uma tarefa.
    override fun onReactivateTaskMenuItemClick(position: Int) {
        val task = deletedTaskList[position]
        mainController.reactivateTask(task)
        Toast.makeText(this, "Tarefa reativada!", Toast.LENGTH_SHORT).show()
    }

    // Métodos da interface não utilizados nesta tela.
    override fun onRemoveTaskMenuItemClick(position: Int) {}
    override fun onEditTaskMenuItemClick(position: Int) {}
    override fun onTaskCheckClick(position: Int, isChecked: Boolean) {}
}