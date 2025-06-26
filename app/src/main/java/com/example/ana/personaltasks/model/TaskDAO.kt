package com.example.ana.personaltasks.model

// Interface que define as operações básicas para manipulação de tarefas no banco de dados
interface TaskDAO {
    suspend fun createTask(task: Task)
    fun retrieveTask(id: Int): Task
    fun retrieveTasks(callback: (List<Task>) -> Unit)
    fun retrieveDeletedTasks(callback: (List<Task>) -> Unit)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    fun searchTasks(query: String): MutableList<Task>
    suspend fun reactivateTask(task: Task)
}