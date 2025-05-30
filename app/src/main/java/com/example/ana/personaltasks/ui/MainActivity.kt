package com.example.ana.personaltasks.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
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

    //Lista de tarefas que serão exibidas no RecyclerView
    private val taskList: MutableList<Task> = mutableListOf()

    //Adapter que irá mostrar a lista de tarefas
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
                    result.data?.getParcelableExtra<Task>(EXTRA_TASK)
                }

                task?.let { receivedTask ->
                    val position = taskList.indexOfFirst { it.id == receivedTask.id }
                    if (position != -1) {
                        taskList[position] = receivedTask
                        mainController.modifyTask(receivedTask)
                    }
                    else {
                        taskList.add(receivedTask)
                        mainController.insertTask(receivedTask)
                    }
                    makeTaskListOrdenated()
                }
            }
        }
        amb.taskRv.adapter = taskAdapter
        amb.taskRv.layoutManager = LinearLayoutManager(this)

        //Inicia a lista de tarefas
        fillTaskList()

    }

    private fun fillTaskList() {
        Thread {
            makeTaskListOrdenated()
        }.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun makeTaskListOrdenated() {
        val tasks = mainController.getTasks()
        // Ordena tarefas pela data limite (parseando a string "dd/MM/yyyy")
        val ordenatedTasks = tasks.sortedWith(compareBy {
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateFormat.parse(it.dataLimite) ?: Date(Long.MAX_VALUE)
            } catch (e: Exception) {
                Date(Long.MAX_VALUE)
            }
        })

        taskList.clear()
        taskList.addAll(ordenatedTasks)

        runOnUiThread {
            taskAdapter.notifyDataSetChanged() //atualiza a lista visual
            updateEmptyTextView()
        }
    }

    private fun updateEmptyTextView() {
        amb.emptyTv.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE
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
        mainController.removeTask(taskList[position].id!!)
        taskList.removeAt(position)
        taskAdapter.notifyItemRemoved(position)
        updateEmptyTextView()
        Toast.makeText(this, "Task removida!", Toast.LENGTH_SHORT).show()
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
}
