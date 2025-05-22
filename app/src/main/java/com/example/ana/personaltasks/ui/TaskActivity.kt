package com.example.ana.personaltasks.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ana.personaltasks.databinding.ActivityTaskBinding
import com.example.ana.personaltasks.model.Constant.EXTRA_TASK
import com.example.ana.personaltasks.model.Constant.EXTRA_VIEW_TASK
import com.example.ana.personaltasks.model.Task

class TaskActivity : AppCompatActivity() {

    private val acb: ActivityTaskBinding by lazy {

        ActivityTaskBinding.inflate(layoutInflater)

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(acb.root)

        setSupportActionBar(acb.toolbarIn.toolbar)
        supportActionBar?.subtitle = "Nova task"

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
                if(viewTask) {

                    supportActionBar?.subtitle = "View task"
                    tituloEt.isEnabled = false
                    descricaoEt.isEnabled = false
                    dataEt.isEnabled = false
                    saveBt.visibility = View.GONE

                }
            }

        }

        with(acb) {

            saveBt.setOnClickListener {
                Task (
                    receivedTask?.id?:hashCode(),
                    tituloEt.text.toString(),
                    descricaoEt.text.toString(),
                    dataEt.text.toString()
                ).let {
                    task ->
                        Intent().apply {
                            putExtra(EXTRA_TASK, task)
                            setResult(RESULT_OK, this)
                        }
                }
                finish()
            }

        }

    }

}