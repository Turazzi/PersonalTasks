package com.example.ana.personaltasks.ui

sealed interface OnTaskClickListener {

    fun onTaskClick(position: Int)
    fun onRemoveTaskMenuItemClick(position: Int)
    fun onEditTaskMenuItemClick(position: Int)

}