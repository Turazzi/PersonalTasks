package com.example.ana.personaltasks.model

import android.renderscript.Sampler.Value
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await


class TaskFirebase : TaskDAO {

    private val auth = FirebaseAuth.getInstance()

    private val database = FirebaseDatabase.getInstance().reference
        .child("users")
        .child(auth.currentUser?.uid ?: "no_user")
        .child("tasks")

    override suspend fun createTask(task: Task) {
        val taskId = database.push().key ?: return
        task.id = taskId
        task.userId = auth.currentUser?.uid
        database.child(taskId).setValue(task).await()
    }

    override fun retrieveTask(id: Int): Task {
        TODO("Not yet implemented")
    }

    override fun retrieveTasks(callback: (List<Task>) -> Unit){
        database.orderByChild("deleted").equalTo(false)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val taskList = snapshot.children.mapNotNull { it.getValue<Task>() }
                    callback(taskList)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override fun retrieveDeletedTasks(callback: (List<Task>) -> Unit) {
        database.orderByChild("deleted").equalTo(true)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val taskList = snapshot.children.mapNotNull { it.getValue<Task>() }
                    callback(taskList)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override suspend fun updateTask(task: Task) {
        task.id?.let { database.child(it).updateChildren(task.toMap()).await() }
    }

    override suspend fun deleteTask(task: Task) {
        task.id?.let { database.child(it).child("deleted").setValue(true).await()}
    }

    override fun searchTasks(query: String): MutableList<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun reactivateTask(task: Task) {
        task.id?.let { database.child(it).child("deleted").setValue(false).await() }
    }


}