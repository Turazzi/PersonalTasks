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

    //Pega a instância principal do serviço de Autenticação do Firebase , ajuda a saber quem e o usuário atual
    private val auth = FirebaseAuth.getInstance()

    //Define o caminho exato onde as tarefas de cada usuário serão guardadas
    //Pega a rai do banco
    private val database = FirebaseDatabase.getInstance().reference
        .child("users")
        //pega o id do usuário logado e cria um nó especifico para ele, se não tiver ninguem logado ele usa o texto no_user
        .child(auth.currentUser?.uid ?: "no_user")
        //a partir do usuário, ele navega para o nó de tasks
        .child("tasks")


    override suspend fun createTask(task: Task) {
        //Persiste uma tarefa ao banco e é uma forma de gerar uma chave única e aleatória para a tarefa
        //Retorna a chave ID gerada , se por algum motivo o id for nulo, a função para
        val taskId = database.push().key ?: return
        task.id = taskId
        //Garante que a chave está associada ao usuário certo
        task.userId = auth.currentUser?.uid
        //Cria um novo nó e guarda a task nele e espera a função salvar sem travar o app
        database.child(taskId).setValue(task).await()
    }

    override fun retrieveTasks(callback: (List<Task>) -> Unit){
        //query que busca apenas as tarefas que não foram deletadas
        database.orderByChild("deleted").equalTo(false)
            //Sempre que algo mudar nos dados, o código em onDataChange será executado novamente
            .addValueEventListener(object: ValueEventListener {
                //Chamado quando os dados são recebidos , o snapshot é uma "cópia" dos dados recebidos
                override fun onDataChange(snapshot: DataSnapshot) {
                    //pega todos os filhos dessa snapshot e converte os dados de volta para Task
                    val taskList = snapshot.children.mapNotNull { it.getValue<Task>() }
                    //Entrega de volta a lista de tarefas atualizada para o MainController
                    callback(taskList)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    //Igual ao anterios, mas pega as tarefas que foram deletadas
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
        //Executa só de o taskId não for nulo, atualiza e transforma em map de novo para enviar de volta
        task.id?.let { database.child(it).updateChildren(task.toMap()).await() }
    }

    //Atualiza o campo deleted de false para true
    override suspend fun deleteTask(task: Task) {
        task.id?.let { database.child(it).child("deleted").setValue(true).await()}
    }

    override suspend fun reactivateTask(task: Task) {
        task.id?.let { database.child(it).child("deleted").setValue(false).await() }
    }

    override suspend fun deletePermanently(task: Task) {
        task.id?.let {database.child(it).removeValue().await()}
    }


}