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
class  TaskAdapter (
    private val taskList: MutableList<Task>,
    private val onTaskClickListener: OnTaskClickListener,
    // Flag para saber qual menu de contexto inflar (principal ou lixeira)
    private val isDeletedList: Boolean = false
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(ttb: TileTaskBinding) : RecyclerView.ViewHolder(ttb.root) {
        val tituloTile: TextView = ttb.tituloTile
        val descricaoTile: TextView = ttb.descricaoTile
        val dataTile: TextView = ttb.dataTile
        val taskCompletedCb: CheckBox = ttb.taskCompletedCb

        init {
            itemView.setOnCreateContextMenuListener { menu, _, _ ->
                val activity = onTaskClickListener as AppCompatActivity
                if (isDeletedList) {
                    // Se estiver na lixeira, infla o menu com a opção de reativar
                    activity.menuInflater.inflate(R.menu.context_menu_deleted, menu)
                    menu.findItem(R.id.reactivate_task_mi)?.setOnMenuItemClickListener {
                        onTaskClickListener.onReactivateTaskMenuItemClick(adapterPosition)
                        true
                    }
                    menu.findItem(R.id.view_task_mi)?.setOnMenuItemClickListener {
                        onTaskClickListener.onTaskClick(adapterPosition)
                        true
                    }
                } else {
                    // Senão, infla o menu principal
                    activity.menuInflater.inflate(R.menu.context_menu_main, menu)
                    menu.findItem(R.id.view_task_mi)?.setOnMenuItemClickListener {
                        onTaskClickListener.onTaskClick(adapterPosition)
                        true
                    }
                    menu.findItem(R.id.edit_task_mi)?.setOnMenuItemClickListener {
                        onTaskClickListener.onEditTaskMenuItemClick(adapterPosition)
                        true
                    }
                    menu.findItem(R.id.remove_task_mi)?.setOnMenuItemClickListener {
                        onTaskClickListener.onRemoveTaskMenuItemClick(adapterPosition)
                        true
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder = TaskViewHolder(
        TileTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.apply {
            tituloTile.text = task.titulo
            descricaoTile.text = task.descricao
            dataTile.text = task.dataLimite
            taskCompletedCb.setOnCheckedChangeListener(null)
            taskCompletedCb.isChecked = task.concluida
            // Desabilita o checkbox se a tarefa estiver na lixeira
            taskCompletedCb.isEnabled = !isDeletedList

            // Aplica ou remove o risco no texto
            tituloTile.paintFlags = if (task.concluida) {
                tituloTile.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tituloTile.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            taskCompletedCb.setOnCheckedChangeListener { _, isChecked ->
                onTaskClickListener.onTaskCheckClick(adapterPosition, isChecked)
            }
        }
    }
}
