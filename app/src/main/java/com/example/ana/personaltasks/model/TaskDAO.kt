package com.example.ana.personaltasks.model

interface TaskDAO {
    fun createTask(task: Task): Long
    fun retrieveTask(id: Int): Task
    fun retrieveTasks(): MutableList<Task>
    fun updateTask(task: Task): Int
    fun deleteTask(id: Int): Int
}