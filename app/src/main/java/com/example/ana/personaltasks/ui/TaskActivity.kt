package com.example.ana.personaltasks.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.ana.personaltasks.R
import com.example.ana.personaltasks.databinding.ActivityTaskBinding
import com.example.ana.personaltasks.model.Constant.EXTRA_TASK
import com.example.ana.personaltasks.model.Constant.EXTRA_VIEW_TASK
import com.example.ana.personaltasks.model.Task
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

class TaskActivity : AppCompatActivity() {

    private val acb: ActivityTaskBinding by lazy {
        ActivityTaskBinding.inflate(layoutInflater)
    }
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val calendar: Calendar = Calendar.getInstance()
    //Guarda a tarefa que foi recebida pela requisição
    private var receivedTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(acb.root)

        findViewById<ImageView>(R.id.toolbar_icon).visibility = View.GONE
        //configura a tollbar com um botao de voltar
        setSupportActionBar(acb.toolbarIn.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //define a data atual no campo de data e configura o clique pra abrir o calendario
        acb.dataEt.setText(dateFormat.format(calendar.time))
        acb.dataEt.setOnClickListener { showDatePickerDialog() }

        //pega a intent e procura por um extra com a chave extra_task  - se encontra é armazenada em receivedTask
        receivedTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TASK, Task::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_TASK)
        }

        //Define os campos a partir do que foi selecionado na view - editar ou visualizar
        if (receivedTask != null) {
            acb.tituloEt.setText(receivedTask!!.titulo)
            acb.descricaoEt.setText(receivedTask!!.descricao)
            acb.dataEt.setText(receivedTask!!.dataLimite)
            acb.cbConcluida.isChecked = receivedTask!!.concluida

            val isViewOnly = intent.getBooleanExtra(EXTRA_VIEW_TASK, false)
            if (isViewOnly) {
                setupViewOnlyMode()
            } else {
                supportActionBar?.subtitle = "Editar tarefa"
            }
        } else {
            supportActionBar?.subtitle = "Nova tarefa"
        }

        acb.saveBt.setOnClickListener { onSave() }
        acb.cancelBt.setOnClickListener { finish() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            calendar.set(y, m, d)
            acb.dataEt.setText(dateFormat.format(calendar.time))
        }, year, month, day).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
            show()
        }
    }

    //define tudo como desabilitado, apenas para visualizacao
    private fun setupViewOnlyMode() {
        supportActionBar?.subtitle = "Detalhes da Tarefa"
        acb.tituloEt.isEnabled = false
        acb.descricaoEt.isEnabled = false
        acb.dataEt.isEnabled = false
        acb.cbConcluida.isEnabled = false
        acb.buttonContainer.visibility = View.GONE
        acb.backBt.visibility = View.VISIBLE
        acb.backBt.setOnClickListener { finish() }
    }

    private fun onSave() {
        val titulo = acb.tituloEt.text.toString().trim()
        val descricao = acb.descricaoEt.text.toString().trim()

        if (titulo.isEmpty()) {
            acb.tituloEt.error = "Título é obrigatório"
            return
        }
        if (descricao.isEmpty()) {
            acb.descricaoEt.error = "Descrição é obrigatória"
            return
        }

        // Se for uma tarefa existente, copia os dados. Senão, cria uma nova com o ID do usuário.
        val taskToSave = receivedTask?.copy(
            titulo = titulo,
            descricao = descricao,
            dataLimite = acb.dataEt.text.toString(),
            concluida = acb.cbConcluida.isChecked
        ) ?: Task(
            titulo = titulo,
            descricao = descricao,
            dataLimite = acb.dataEt.text.toString(),
            concluida = acb.cbConcluida.isChecked,
            userId = FirebaseAuth.getInstance().currentUser?.uid
        )

        // Devolve a tarefa para a MainActivity, que se encarrega de salvar no Firebase.
        val resultIntent = Intent().putExtra(EXTRA_TASK, taskToSave)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
