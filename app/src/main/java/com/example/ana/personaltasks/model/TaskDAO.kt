package com.example.ana.personaltasks.model

// Interface que define as operações básicas para manipulação de tarefas no banco de dados
interface TaskDAO {
    fun createTask(task: Task): Long
    fun retrieveTask(id: Int): Task
    fun retrieveTasks(): MutableList<Task>
    fun updateTask(task: Task): Int
    fun deleteTask(id: Int): Int
}