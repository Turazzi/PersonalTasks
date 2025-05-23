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
import java.text.SimpleDateFormat
import java.util.Locale

class TaskActivity : AppCompatActivity() {

    // Binding da tela, acesso direto aos elementos da UI sem precisar de findViewById
    private val acb: ActivityTaskBinding by lazy {
        ActivityTaskBinding.inflate(layoutInflater)
    }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(acb.root)

        // Esconde o ícone da toolbar que seria usado para adicionar
        val toolbarIcon = findViewById<ImageView>(R.id.toolbar_icon)
        toolbarIcon.visibility = View.GONE

        setSupportActionBar(acb.toolbarIn.toolbar)
        supportActionBar?.subtitle = "Nova task"

        // Preenche campo de data com a data atual
        acb.dataEt.setText(dateFormat.format(calendar.time))

        // Configura comportamento do campo de data (desabilita teclado e mostra DatePicker)
        acb.dataEt.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    this@TaskActivity,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        calendar.set(selectedYear, selectedMonth, selectedDay)
                        setText(dateFormat.format(calendar.time))
                    },
                    year, month,day
                )

                // Impede que o usuário escolha datas anteriores à atual
                datePickerDialog.datePicker.minDate = System.currentTimeMillis()
                datePickerDialog.show()
            }
        }

        // Verifica se recebeu uma task via intent para edição ou visualização
        val receivedTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TASK, Task::class.java)
        }
        else {
            intent.getParcelableExtra<Task>(EXTRA_TASK)
        }

        // Se tiver recebido uma task:
        receivedTask?.let{

            // Altera subtítulo para "Editar"
            supportActionBar?.subtitle = "Editar"
            with(acb) {
                tituloEt.setText(it.titulo)
                descricaoEt.setText(it.descricao)
                dataEt.setText(it.dataLimite)

                // Se for só visualização
                val viewTask = intent.getBooleanExtra(EXTRA_VIEW_TASK, false)
                if(viewTask) run {

                    supportActionBar?.subtitle = "View task"

                    // Desabilita campos de edição
                    tituloEt.isEnabled = false
                    descricaoEt.isEnabled = false
                    dataEt.isEnabled = false

                    saveBt.visibility = View.GONE
                    cancelBt.visibility = View.GONE
                    backBt.visibility = View.VISIBLE

                    // Botão de voltar leva de volta pro MainActivity
                    backBt.setOnClickListener {
                        val intent = Intent(this@TaskActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }

                }
            }

        }

        // Lógica dos botões de salvar e cancelar
        with(acb) {

            saveBt.setOnClickListener {

                val titulo = tituloEt.text.toString().trim()
                val descricao = descricaoEt.text.toString().trim()

                if(titulo.isEmpty()) {
                    tituloEt.error = "Titulo é obrigatório"
                    tituloEt.requestFocus()
                    return@setOnClickListener
                }

                if (descricao.isEmpty()) {
                    descricaoEt.error = "Descrição é obrigatória"
                    descricaoEt.requestFocus()
                    return@setOnClickListener
                }

                // Cria ou atualiza a task
                val task = Task (
                    receivedTask?.id?:hashCode(), // gera ID novo se não tiver vindo um
                    tituloEt.text.toString(),
                    descricaoEt.text.toString(),
                    dataEt.text.toString()
                )

                // Envia a task de volta para MainActivity
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_TASK, task)
                }

                setResult(RESULT_OK, resultIntent)
                finish()

            }

            // Cancelar leva de volta para a tela principal
            cancelBt.setOnClickListener {
                val intent = Intent(this@TaskActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

        }

    }

}