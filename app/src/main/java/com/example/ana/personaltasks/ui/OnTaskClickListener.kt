package com.example.ana.personaltasks.ui

sealed interface OnTaskClickListener {
    fun onTaskClick(position: Int)
    fun onRemoveTaskMenuItemClick(position: Int)
    fun onEditTaskMenuItemClick(position: Int)
    fun onTaskCheckClick(position: Int, isChecked: Boolean)
    fun onReactivateTaskMenuItemClick(position: Int)
    fun onPermanentDeletTaskMenuItemClick(position: Int)
    fun onPriorityTaskCheckClick(position: Int, isChecked: Boolean)
}