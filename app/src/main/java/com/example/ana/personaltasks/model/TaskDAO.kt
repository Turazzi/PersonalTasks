package com.example.ana.personaltasks.model

// Interface que define as operações básicas para manipulação de tarefas no banco de dados
interface TaskDAO {
    suspend fun createTask(task: Task)
    fun retrieveTasks(callback: (List<Task>) -> Unit)
    fun retrieveDeletedTasks(callback: (List<Task>) -> Unit)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun reactivateTask(task: Task)
    suspend fun deletePermanently(task: Task)
}