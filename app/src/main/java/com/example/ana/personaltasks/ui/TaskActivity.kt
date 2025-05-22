package com.example.ana.personaltasks.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.ana.personaltasks.R
import com.example.ana.personaltasks.databinding.ActivityTaskBinding
import com.example.ana.personaltasks.model.Constant.EXTRA_TASK
import com.example.ana.personaltasks.model.Constant.EXTRA_VIEW_TASK
import com.example.ana.personaltasks.model.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskActivity : AppCompatActivity() {

    private val acb: ActivityTaskBinding by lazy {

        ActivityTaskBinding.inflate(layoutInflater)

    }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(acb.root)

        setSupportActionBar(acb.toolbarIn.toolbar)
        supportActionBar?.subtitle = "Nova task"

        acb.dataEt.setText(dateFormat.format(calendar.time))

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

                datePickerDialog.datePicker.minDate = System.currentTimeMillis()
                datePickerDialog.show()
            }
        }

        val receivedTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            intent.getParcelableExtra(EXTRA_TASK, Task::class.java)

        }
        else {

            intent.getParcelableExtra<Task>(EXTRA_TASK)

        }

        receivedTask?.let{

            supportActionBar?.subtitle = "Editar"
            with(acb) {
                tituloEt.setText(it.titulo)
                descricaoEt.setText(it.descricao)
                dataEt.setText(it.dataLimite)

                val viewTask = intent.getBooleanExtra(EXTRA_VIEW_TASK, false)
                if(viewTask) run {

                    supportActionBar?.subtitle = "View task"
                    tituloEt.isEnabled = false
                    descricaoEt.isEnabled = false
                    dataEt.isEnabled = false
                    saveBt.visibility = View.GONE
                    backBt.visibility = View.VISIBLE

                    backBt.setOnClickListener {
                        val intent = Intent(this@TaskActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }

                }
            }

        }

        with(acb) {

            saveBt.setOnClickListener {
                val task = Task (
                    receivedTask?.id?:hashCode(),
                    tituloEt.text.toString(),
                    descricaoEt.text.toString(),
                    dataEt.text.toString()
                )

                val resultIntent = Intent().apply {
                    putExtra(EXTRA_TASK, task)
                }

                setResult(RESULT_OK, resultIntent)
                finish()

            }

            cancelBt.setOnClickListener {
                val intent = Intent(this@TaskActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

        }

    }

}