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

class  TaskAdapter (
    //Instruções iniciais
    //Lista de tarefas que serão exibidas
    private val taskList: MutableList<Task>,
    //Notifica a classe se o usuário fizer alguma coisa
    private val onTaskClickListener: OnTaskClickListener,
    //Permite que o adapter se comporte de maneiras diferentes - lida com as apagadas e com as "normais"
    private val isDeletedList: Boolean = false
    //Indica que a classe herda todas as funcionalidades e responsabilidades do adaptador
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(ttb: TileTaskBinding) : RecyclerView.ViewHolder(ttb.root) {
        val tituloTile: TextView = ttb.tituloTile
        val descricaoTile: TextView = ttb.descricaoTile
        val dataTile: TextView = ttb.dataTile
        val taskCompletedCb: CheckBox = ttb.taskCompletedCb

        //Executado assim que um viewHolder é criado , é aqui que configura os listeners que são os mesmos para todos os itens
        init {
            //configura o que acontece quando da um clique longo no item
            itemView.setOnCreateContextMenuListener { menu, _, _ ->
                val activity = onTaskClickListener as AppCompatActivity
                //se for true, ele infla a view da lixeira
                if (isDeletedList) {
                    activity.menuInflater.inflate(R.menu.context_menu_deleted, menu)
                    menu.findItem(R.id.reactivate_task_mi)?.setOnMenuItemClickListener {
                        onTaskClickListener.onReactivateTaskMenuItemClick(adapterPosition)
                        true
                    }
                    menu.findItem(R.id.view_task_mi)?.setOnMenuItemClickListener {
                        onTaskClickListener.onTaskClick(adapterPosition)
                        true
                    }
                    menu.findItem(R.id.permanent_delet_task_mi)?.setOnMenuItemClickListener {
                        onTaskClickListener.onPermanentDeletTaskMenuItemClick(adapterPosition)
                        true
                    }
                    //se for false, ele infla a view da main
                } else {
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

    //Cria um view holder novo e vazio
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder = TaskViewHolder(
        TileTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = taskList.size

    //Pega os dados de uma tarefa específica e "amarra" (bind) aos componentes visuais da view
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        //Seta os atributos da tarefa na view
        holder.apply {
            tituloTile.text = task.titulo
            descricaoTile.text = task.descricao
            dataTile.text = task.dataLimite
            //Limpa o listener antigo para evitar seu acionamento acidental
            taskCompletedCb.setOnCheckedChangeListener(null)
            taskCompletedCb.isChecked = task.concluida
            //desabilita se estiver na lixeira
            taskCompletedCb.isEnabled = !isDeletedList

            //Se o chackbox estiver ativado, adiciona um risco no título da task
            tituloTile.paintFlags = if (task.concluida) {
                tituloTile.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tituloTile.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            //Se o usuário clica na checkbox, ele chama a função onTaskClickListener da Activity, passando a posição e o novo estado da tarefa
            taskCompletedCb.setOnCheckedChangeListener { _, isChecked ->
                onTaskClickListener.onTaskCheckClick(adapterPosition, isChecked)
            }
        }
    }
}
