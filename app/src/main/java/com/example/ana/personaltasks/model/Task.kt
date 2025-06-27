package com.example.ana.personaltasks.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

//Data class que representa as tarefas
// Implementa Parcelable para permitir passagem fácil entre Activities via Intent - permite a automação desse processo
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
    //Anotação específica do firebase
    //Se não colocar o exclude, o firebase reconhece a função como um dado que precisa ser salvo
    @Exclude
    //"Traduz" os dados para o Firebase, passa as instruções dos dados pra ele
    //Para usar o updateChildren() é obrigatório receber os dados em formato de map e essa função faz a tradução de Task pra Map necessária
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