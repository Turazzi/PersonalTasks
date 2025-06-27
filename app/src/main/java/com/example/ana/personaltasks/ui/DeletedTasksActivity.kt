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
import androidx.appcompat.app.AlertDialog
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

//Define que essa tela ouvinte de cliques  e de pesquisa
class DeletedTasksActivity: AppCompatActivity(), OnTaskClickListener, SearchView.OnQueryTextListener {

    //Ponte segura para os componentes visuais da tela
    private val binding: ActivityDeletedTasksBinding by lazy {
        ActivityDeletedTasksBinding.inflate(layoutInflater)
    }

    //guarda a lista que vai ser mostrada na tela
    private val deletedTaskList: MutableList<Task> = mutableListOf()
    //guarda todos os dados que chegam do firebase
    private val allDeletedTasks: MutableList<Task> = mutableListOf()

    //Mesma utilizada na main, mas configurada para aparecer so a lista de tasks deletadas
    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(deletedTaskList, this, isDeletedList = true)
    }

    //Usado para pedir a lista de tarefas deletadas
    private val mainController: MainController by lazy {
        MainController()
    }

    //guardam o estado atual dos filtros
    private var currentSearchQuery: String? = null
    private var selectedDateFilter: String? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //configura a toolbar
        setSupportActionBar(binding.toolbarIn.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //tira o icone de + da toolbar
        binding.toolbarIn.toolbar.findViewById<ImageView>(R.id.toolbar_icon).visibility = View.GONE

        binding.deletedTasksRv.layoutManager = LinearLayoutManager(this)
        binding.deletedTasksRv.adapter = taskAdapter

        //Chama as tasks deletadas, guarda na lista e chama para exibição
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

    //infla o menu da tela de deletadas, com os icones de pesquisa
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

    //Chamado quando o usuário clica em um ícone do menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter_by_date_deleted -> {
                showDatePickerDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Mostra uma caixa de diálogo para o usuário selecionar uma data, quando é selecionada a função de filtro é chamada para mostrar so as tarefas pedidas na pesquisa
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

    //é chamada a cada letra digitada na barra de pesquisa
    override fun onQueryTextChange(newText: String?): Boolean {
        currentSearchQuery = newText
        filterDeletedTasks()
        return true
    }

    //olha para os filtros e decide qual subconjunto de lista de tarefas deve ser chamado
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


    //Define o que fazer em um clique curto - mostra os detalhes da tarefa
    override fun onTaskClick(position: Int) {
        val intent = Intent(this, TaskActivity::class.java).apply {
            putExtra(Constant.EXTRA_TASK, deletedTaskList[position])
            putExtra(Constant.EXTRA_VIEW_TASK, true)
        }
        startActivity(intent)
    }

    //Move a tarefa novamente para a main - reativando-a
    override fun onReactivateTaskMenuItemClick(position: Int) {
        val task = deletedTaskList[position]
        mainController.reactivateTask(task)
        Toast.makeText(this, "Tarefa reativada!", Toast.LENGTH_SHORT).show()
    }

    override fun onPermanentDeletTaskMenuItemClick(position: Int) {
        val task = deletedTaskList[position]

        AlertDialog.Builder(this)
            .setTitle("Excluir Permanentemente")
            .setMessage("Tem certeza de que deseja excluir a tarefa '${task.titulo}'? Esta ação não pode ser desfeita.")
            .setPositiveButton("Excluir") {_, _ ->
                mainController.permanentDeleteTask(task)
                Toast.makeText(this, "Tarefa excluída permanentemente", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }




    //opcoes que nao sao usadas aqui
    override fun onRemoveTaskMenuItemClick(position: Int) {}
    override fun onEditTaskMenuItemClick(position: Int) {}
    override fun onTaskCheckClick(position: Int, isChecked: Boolean) {}
    override fun onPriorityTaskCheckClick(position: Int, isChecked: Boolean) {
    }
}