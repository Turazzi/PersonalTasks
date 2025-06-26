package com.example.ana.personaltasks.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
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
            Log.d("DeletedTasks", "Dados recebidos do Firebase: ${tasks.size} itens")
            val sortedTasks = tasks.sortedWith(compareBy {
                try {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.dataLimite) ?: Date(Long.MAX_VALUE)
                } catch (e: Exception) {
                    Date(Long.MAX_VALUE)
                }
            })
            allDeletedTasks.clear()
            allDeletedTasks.addAll(sortedTasks)
            updateDisplayedList(currentSearchQuery)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.deleted_tasks_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search_deleted)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        currentSearchQuery = newText
        updateDisplayedList(newText)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateDisplayedList(query: String?) {
        Log.d("DeletedTasks", "Filtrando a lista com o texto: '$query'")

        // 1. Determina a lista a ser exibida (filtrada ou completa)
        val listToShow = if (query.isNullOrBlank()) {
            allDeletedTasks // Mostra tudo se a busca for vazia
        } else {
            // Cria uma nova lista com os resultados do filtro
            val filteredList = mutableListOf<Task>()
            for (task in allDeletedTasks) {
                val matchesTitle = task.titulo.contains(query, ignoreCase = true)

                // Log para cada tarefa para ver porque ela foi ou não incluída
                Log.d("DeletedTasks", "Verificando Tarefa: '${task.titulo}'. Título corresponde? $matchesTitle.")

                if (matchesTitle) {
                    filteredList.add(task)
                }
            }
            filteredList
        }

        Log.d("DeletedTasks", "A lista a ser exibida tem ${listToShow.size} itens.")

        // 2. Atualiza a lista do adapter e notifica a mudança
        deletedTaskList.clear()
        deletedTaskList.addAll(listToShow)
        taskAdapter.notifyDataSetChanged()

        // 3. Atualiza a mensagem de "lista vazia"
        binding.emptyDeletedTv.visibility =
            if (deletedTaskList.isEmpty()) View.VISIBLE else View.GONE
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