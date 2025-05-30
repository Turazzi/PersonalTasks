package com.example.ana.personaltasks.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ana.personaltasks.R
import com.example.ana.personaltasks.databinding.TileTaskBinding
import com.example.ana.personaltasks.model.Task
import com.example.ana.personaltasks.ui.OnTaskClickListener

//Classe da lista de tarefas, muito útil para mostrar diversos itens
class TaskAdapter (

    private val taskList: MutableList<Task>,
    private val onTaskClickListener: OnTaskClickListener //para passar as instruções quando o usuário clica ou interage com algum item da lista

): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    //Representa o layout individual de cada item da lista
    inner class TaskViewHolder(ttb: TileTaskBinding): RecyclerView.ViewHolder(ttb.root) {

        //Referências aos valores de cada tarefa da lista
        val tituloTile: TextView = ttb.tituloTile
        val descricaoTile: TextView = ttb.descricaoTile
        val dataTile: TextView = ttb.dataTile
        val taskCompletedCb: CheckBox = ttb.statusCkb

        init {

            //Define o menu que aparece ao segurar item da lista
            ttb.root.setOnCreateContextMenuListener { menu, v, menuInfo ->
                //Infla o menu
                (onTaskClickListener as AppCompatActivity).menuInflater.inflate(R.menu.context_menu_main, menu)

                //Configura o clique de cada item do menu e chama seus respectivos métodos
                menu.findItem(R.id.view_task_mi).setOnMenuItemClickListener {
                    onTaskClickListener.onTaskClick(adapterPosition)
                    true
                }
                menu.findItem(R.id.edit_task_mi).setOnMenuItemClickListener {
                    onTaskClickListener.onEditTaskMenuItemClick(adapterPosition)
                    true
                }
                menu.findItem((R.id.remove_task_mi)).setOnMenuItemClickListener {
                    onTaskClickListener.onRemoveTaskMenuItemClick(adapterPosition)
                    true
                }
            }

            //Define o clique normal, que não tem funcionalidade alguma aqyi no programa
            ttb.root.setOnClickListener {}

        }

    }

    // Cria o ViewHolder inflando o layout TileTaskBinding para cada item da lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder = TaskViewHolder(
        TileTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount(): Int = taskList.size

    //Liga os dados da tarefa à view do ViewHolder na posição especificada

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
//        taskList[position].let { task ->
//            with(holder) {
//                tituloTile.text = task.titulo  //seta o título da tarefa no TextView
//                descricaoTile.text = task.descricao
//                dataTile.text = task.dataLimite
//            }
//        }

        holder.apply {
            tituloTile.text = task.titulo
            descricaoTile.text = task.descricao
            dataTile.text = task.dataLimite

            taskCompletedCb.setOnCheckedChangeListener(null)

            taskCompletedCb.isChecked = task.concluida

            if (task.concluida) {
                tituloTile.paintFlags = tituloTile.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tituloTile.paintFlags = tituloTile.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            taskCompletedCb.setOnCheckedChangeListener { _, isChecked ->
                onTaskClickListener.onTaskCheckClick(adapterPosition, isChecked)
            }
        }
    }



}