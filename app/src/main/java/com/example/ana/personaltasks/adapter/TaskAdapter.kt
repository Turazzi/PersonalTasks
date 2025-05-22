package com.example.ana.personaltasks.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ana.personaltasks.R
import com.example.ana.personaltasks.databinding.TileTaskBinding
import com.example.ana.personaltasks.model.Task
import com.example.ana.personaltasks.ui.OnTaskClickListener

class TaskAdapter (

    private val taskList: MutableList<Task>,
    private val onTaskClickListener: OnTaskClickListener

): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(ttb: TileTaskBinding): RecyclerView.ViewHolder(ttb.root) {

        val tituloTile: TextView = ttb.tituloTile
        val descricaoTile: TextView = ttb.descricaoTile
        val dataTile: TextView = ttb.dataTile

        init {

            ttb.root.setOnCreateContextMenuListener { menu, v, menuInfo ->
                (onTaskClickListener as AppCompatActivity).menuInflater.inflate(R.menu.context_menu_main, menu)
                menu.findItem(R.id.edit_task_mi).setOnMenuItemClickListener {
                    onTaskClickListener.onEditTaskMenuItemClick(adapterPosition)
                    true
                }
                menu.findItem((R.id.remove_task_mi)).setOnMenuItemClickListener {
                    onTaskClickListener.onRemoveTaskMenuItemClick(adapterPosition)
                    true
                }
            }

            ttb.root.setOnClickListener {onTaskClickListener.onTaskClick(adapterPosition)}

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}