package com.example.ana.personaltasks.ui

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ana.personaltasks.R
import com.example.ana.personaltasks.adapter.TaskAdapter
import com.example.ana.personaltasks.controller.MainController
import com.example.ana.personaltasks.databinding.ActivityMainBinding
import com.example.ana.personaltasks.model.Constant.EXTRA_TASK
import com.example.ana.personaltasks.model.Constant.EXTRA_VIEW_TASK
import com.example.ana.personaltasks.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), OnTaskClickListener {

    // View Binding pra acessar a UI de forma segura e fácil
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    //Lista de tarefas que serão exibidas no RecyclerView (lista de exibição)
    private val taskList: MutableList<Task> = mutableListOf()
    //Lista de backup com todas as tarefas
    private val originalTaskList: MutableList<Task> = mutableListOf()

    //Adapter que irá mostrar a lista de tarefas
    // PONTO CRÍTICO 1: O Adapter DEVE observar a 'taskList'
    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(taskList, this)
    }

    // Launcher para abrir outras Activities e receber resultado (ex: criação/edição de tarefa)
    private lateinit var acResult: ActivityResultLauncher<Intent>

    //Controller que encapsula a lógica de negócio - CRUD
    private val mainController: MainController by lazy {
        MainController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        //Configura a toolbar sem título
        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Pega o botão de adicionar tarefa na toolbar e configura clique para abrir a TaskActivity
        val addButton = findViewById<ImageView>(R.id.toolbar_icon)
        addButton.setOnClickListener {
            acResult.launch(Intent(this, TaskActivity::class.java))
        }

        // Registra um listener para receber resultados da TaskActivity
        acResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(EXTRA_TASK, Task::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    result.data?.getParcelableExtra<Task>(EXTRA_TASK)
                }

                task?.let { receivedTask ->
                    val position = originalTaskList.indexOfFirst { it.id == receivedTask.id }

                    if (position != -1) {
                        mainController.modifyTask(receivedTask)
                    }
                    else {
                        mainController.insertTask(receivedTask)
                    }
                    fillTaskList()
                }
            }
        }

        // Configura RecyclerView com adapter e layout manager linear vertical
        amb.taskRv.adapter = taskAdapter
        amb.taskRv.layoutManager = LinearLayoutManager(this)

        //Inicia a lista de tarefas
        fillTaskList()
    }

    private fun updateNumberOfTasks() {
        val tasksCount = taskList.size
        amb.contadorTv.text = "Tarefas: Você tem $tasksCount tarefas"
    }

    private fun fillTaskList() {
        Thread {
            makeTaskListOrdenated()
        }.start()
    }

    private fun makeTaskListOrdenated() {
        val tasks = mainController.getTasks()
        // Ordena tarefas pela data limite (parseando a string "dd/MM/yyyy")
        val ordenatedTasks = tasks.sortedWith(compareBy {
            try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.dataLimite) ?: Date(Long.MAX_VALUE)
            } catch (e: Exception) {
                Date(Long.MAX_VALUE)
            }
        })

        originalTaskList.clear()
        taskList.clear()
        originalTaskList.addAll(ordenatedTasks)
        taskList.addAll(ordenatedTasks)

        runOnUiThread {
            taskAdapter.notifyDataSetChanged() //atualiza a lista visual
            updateEmptyTextView()
            updateNumberOfTasks()
        }
    }

    private fun updateEmptyTextView() {
        amb.emptyTv.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE // PONTO CRÍTICO 3: Usa a 'taskList'
    }

    // Interface OnTaskClickListener: usuário clicou na tarefa -> abre para visualizar
    override fun onTaskClick(position: Int) {
        Intent(this, TaskActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            putExtra(EXTRA_VIEW_TASK, true)
            startActivity(this)
        }
    }

    // Clique no menu remover: deleta do banco, remove da lista e atualiza UI
    override fun onRemoveTaskMenuItemClick(position: Int) {
        val taskToRemove = taskList[position]

        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza de que deseja excluir a tarefa? '${taskToRemove.titulo}'?")
            .setPositiveButton("Sim") {
                _,_ ->
                mainController.removeTask(taskToRemove.id!!)

                taskList.remove(taskToRemove)
                originalTaskList.remove(taskToRemove)

                taskAdapter.notifyItemRemoved(position)

                updateEmptyTextView()
                updateNumberOfTasks()
                Toast.makeText(this, "Task removida!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    // Clique no menu editar: abre a TaskActivity para edição da tarefa
    override fun onEditTaskMenuItemClick(position: Int) {
        Intent(this, TaskActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            acResult.launch(this)
        }
    }

    override fun onTaskCheckClick(position: Int, isChecked: Boolean) {
        val task = taskList[position]
        task.concluida = isChecked
        mainController.modifyTask(task)
        taskAdapter.notifyItemChanged(position)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterTasks(query.orEmpty())
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    filterTasks("")
                }
                return true
            }
        })
        return true
    }

    private fun filterTasks(query: String) {
        if (query.isEmpty()) {
            taskList.clear()
            taskList.addAll(originalTaskList)
            taskAdapter.notifyDataSetChanged()
            updateNumberOfTasks()
        } else {
            Thread {
                val filteredList = mainController.findTasks(query)
                runOnUiThread {
                    taskList.clear()
                    taskList.addAll(filteredList)
                    taskAdapter.notifyDataSetChanged()
                    updateNumberOfTasks()
                }
            }.start()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}