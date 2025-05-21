package com.example.ana.personaltasks.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.ana.personaltasks.R
import java.sql.SQLException

class TaskSqlite (context: Context): TaskDAO {

    companion object {

        private val TASK_DATABASE_FILE = "personalTasks"
        private val TASK_TABLE = "tasks"
        private val ID_COLUMN = "id"
        private val TITULO_COLUMN = "titulo"
        private val DESCRICAO_COLUMN = "descricao"
        private val DATA_LIMITE_COLUMN = "data_limite"

        val CREATE_TASK_TABLE = "CREATE TABLE IF NOT EXISTS $TASK_TABLE (" +
                "$ID_COLUMN INTEGER NOT NULL PRIMARY KEY, " +
                "$TITULO_COLUMN TEXT NOT NULL, " +
                "$DESCRICAO_COLUMN TEXT NOT NULL, " +
                "$DATA_LIMITE_COLUMN TEXT NOT NULL );"
    }

    private val taskDatabase: SQLiteDatabase = context.openOrCreateDatabase(
        TASK_DATABASE_FILE,
        MODE_PRIVATE,
        null
    )

    init {
        try {
            taskDatabase.execSQL(CREATE_TASK_TABLE)
        }
        catch (se: SQLException) {
            Log.e(context.getString(R.string.app_name), se.message.toString())
        }

    }

    override fun createTask(task: Task): Long {
        TODO("Not yet implemented")
    }

    override fun retrieveTask(id: Int): Task {
        TODO("Not yet implemented")
    }

    override fun retrieveTasks(): MutableList<Task> {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: Task): Int {
        TODO("Not yet implemented")
    }

    override fun deleteTask(id: Int): Int {
        TODO("Not yet implemented")
    }
}