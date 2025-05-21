package com.example.ana.personaltasks.model

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.room.util.getColumnIndexOrThrow
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


    override fun createTask(task: Task): Long =
            taskDatabase.insert(TASK_TABLE, null, task.toContentValues())

    override fun retrieveTask(id: Int): Task {
        val cursor = taskDatabase.query(
                true,
        TASK_TABLE,
        null,
        "$ID_COLUMN = ?",
        arrayOf(id.toString()),
        null,
        null,
        null,
        null
        )

        return if (cursor.moveToFirst())
            cursor.toTask()

        else
            Task()

    }

    override fun retrieveTasks(): MutableList<Task> {
        val taskList: MutableList<Task> = mutableListOf()

        val cursor = taskDatabase.rawQuery("SELECT * FROM $TASK_TABLE;", null)

        while (cursor.moveToNext())
            taskList.add(cursor.toTask())

        return taskList
    }

    override fun updateTask(task: Task) = taskDatabase.update(
        TASK_TABLE,
        task.toContentValues(),
        "$ID_COLUMN = ?",
        arrayOf(task.id.toString())
    )

    override fun deleteTask(id: Int) = taskDatabase.delete(
        TASK_TABLE,
        "$ID_COLUMN = ?",
        arrayOf(id.toString())
    )

    private fun Task.toContentValues() = ContentValues().apply {

        put(ID_COLUMN, id)
        put(TITULO_COLUMN, titulo)
        put(DESCRICAO_COLUMN, descricao)
        put(DATA_LIMITE_COLUMN, dataLimite)

    }

    private fun Cursor.toTask() = Task(
        getInt(getColumnIndexOrThrow(ID_COLUMN)),
        getString(getColumnIndexOrThrow(TITULO_COLUMN)),
        getString(getColumnIndexOrThrow(DESCRICAO_COLUMN)),
        getString(getColumnIndexOrThrow(DATA_LIMITE_COLUMN))
    )

}