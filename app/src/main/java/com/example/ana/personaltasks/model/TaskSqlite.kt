package com.example.ana.personaltasks.model

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.ana.personaltasks.R
import java.sql.SQLException

// Implementação do DAO usando SQLite para persistência local das tarefas
class  TaskSqlite (context: Context): TaskDAO {

    companion object {

        private val TASK_DATABASE_FILE = "personalTasks"
        private val TASK_TABLE = "tasks"
        private val ID_COLUMN = "id"
        private val TITULO_COLUMN = "titulo"
        private val DESCRICAO_COLUMN = "descricao"
        private val DATA_LIMITE_COLUMN = "data_limite"
        private val CONCLUIDA_COLUMN = "concluida"

        val CREATE_TASK_TABLE = "CREATE TABLE IF NOT EXISTS $TASK_TABLE (" +
                "$ID_COLUMN INTEGER NOT NULL PRIMARY KEY, " +
                "$TITULO_COLUMN TEXT NOT NULL, " +
                "$DESCRICAO_COLUMN TEXT NOT NULL, " +
                "$DATA_LIMITE_COLUMN TEXT NOT NULL, " +
                "$CONCLUIDA_COLUMN INTEGER NOT NULL DEFAULT 0);"
    }

    // Banco de dados SQLite aberto/criado no modo privado do app
    private val taskDatabase: SQLiteDatabase = context.openOrCreateDatabase(
        TASK_DATABASE_FILE,
        MODE_PRIVATE,
        null
    )

    init {
        try {
            // Executa o comando SQL para criar a tabela (se ainda não existir)
            taskDatabase.execSQL(CREATE_TASK_TABLE)
        }
        catch (se: SQLException) {
            Log.e(context.getString(R.string.app_name), se.message.toString())
        }

    }

    // Insere uma nova tarefa na tabela e retorna o ID gerado (ou -1 se falhar)
    override fun createTask(task: Task): Long =
            taskDatabase.insert(TASK_TABLE, null, task.toContentValues())

    // Busca uma tarefa pelo ID. Se não encontrar, retorna uma tarefa vazia (default)
    override fun retrieveTask(id: Int): Task {
        val cursor = taskDatabase.query(
            true,                // distinct: se true, elimina linhas duplicadas
            TASK_TABLE,                 // table: nome da tabela onde buscar
            null,               // columns: quais colunas buscar (null = todas)
            "$ID_COLUMN = ?",  // selection: cláusula WHERE, ? é placeholder para parâmetro
            arrayOf(id.toString()),     // selectionArgs: valores que substituem os '?' na seleção (protege contra SQL Injection)
            null,               // groupBy: agrupar resultados (não usado aqui)
            null,               // having: filtro para grupo (não usado)
            null,               // orderBy: ordenação dos resultados (não usado)
            null                 // limit: limite de resultados (não usado)
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
        TASK_TABLE,                         // tabela onde atualizar
        task.toContentValues(),              // valores que serão atualizados
        "$ID_COLUMN = ?",          // cláusula WHERE para indicar qual registro atualizar
        arrayOf(task.id.toString())         // valores que substituem o '?' na cláusula WHERE

    )

    override fun deleteTask(id: Int) = taskDatabase.delete(
        TASK_TABLE,                             // tabela onde deletar
        "$ID_COLUMN = ?",           // cláusula WHERE para indicar qual registro deletar
        arrayOf(id.toString())                   // valores que substituem o '?' na cláusula WHERE

    )

    private fun Task.toContentValues() = ContentValues().apply {

        put(ID_COLUMN, id)
        put(TITULO_COLUMN, titulo)
        put(DESCRICAO_COLUMN, descricao)
        put(DATA_LIMITE_COLUMN, dataLimite)
        put(CONCLUIDA_COLUMN, if (concluida) 1 else 0)

    }

    private fun Cursor.toTask() = Task(
        getInt(getColumnIndexOrThrow(ID_COLUMN)),
        getString(getColumnIndexOrThrow(TITULO_COLUMN)),
        getString(getColumnIndexOrThrow(DESCRICAO_COLUMN)),
        getString(getColumnIndexOrThrow(DATA_LIMITE_COLUMN)),
        getInt(getColumnIndexOrThrow(CONCLUIDA_COLUMN)) == 1
    )

}
