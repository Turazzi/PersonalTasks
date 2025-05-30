package com.example.ana.personaltasks.model

import android.os.Parcelable
import com.example.ana.personaltasks.model.Constant.INVALID_TASK_ID
import kotlinx.parcelize.Parcelize

//Data class que representa as tarefas
// Implementa Parcelable para permitir passagem fácil entre Activities via Intent
@Parcelize
data class Task(
    var id: Int? = INVALID_TASK_ID,
    var titulo: String = " ",
    var descricao: String = " ",
    var dataLimite: String = "",
    var concluida: Boolean = false
) : Parcelable
