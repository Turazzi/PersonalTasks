package com.example.ana.personaltasks.model

import android.os.Parcelable
import com.example.ana.personaltasks.model.Constant.INVALID_TASK_ID
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

//Data class que representa as tarefas
// Implementa Parcelable para permitir passagem fácil entre Activities via Intent
@Parcelize
data class Task(
    var id: String? = null,
    var titulo: String = "",
    var descricao: String = "",
    var dataLimite: String = "",
    var concluida: Boolean = false,
    var deleted: Boolean = false,
    var userId: String? = null
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "titulo" to titulo,
            "descricao" to descricao,
            "dataLimite" to dataLimite,
            "concluida" to concluida,
            "deleted" to deleted,
            "userId" to userId
        )
    }
}