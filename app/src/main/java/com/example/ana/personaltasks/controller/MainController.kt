package com.example.ana.personaltasks.controller

import androidx.room.RoomDatabase
import com.example.ana.personaltasks.model.Task
import com.example.ana.personaltasks.model.TaskDAO
import com.example.ana.personaltasks.model.TaskFirebase
import com.example.ana.personaltasks.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Controlador principal que serve como intermediário entre a interface (MainActivity) e o acesso a dados (DAO)
class MainController {

    private val taskDAO: TaskDAO = TaskFirebase()

    fun insertTask(task: Task) = CoroutineScope(Dispatchers.IO).launch {
        taskDAO.createTask(task)
    }

    fun getTasks(callback: (List<Task>) -> Unit) {
        taskDAO.retrieveTasks(callback)
    }

    fun getDeletedTasks(callback: (List<Task>) -> Unit) {
        taskDAO.retrieveDeletedTasks(callback)
    }

    fun modifyTask(task: Task) = CoroutineScope(Dispatchers.IO).launch {
        taskDAO.updateTask(task)
    }

    fun removeTask(task: Task) = CoroutineScope(Dispatchers.IO).launch {
        taskDAO.deleteTask(task)
    }

    fun reactivateTask(task: Task) = CoroutineScope(Dispatchers.IO).launch {
        taskDAO.reactivateTask(task)
    }

}