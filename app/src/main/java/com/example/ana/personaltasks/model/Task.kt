package com.example.ana.personaltasks.model

import android.os.Parcelable
import com.example.ana.personaltasks.model.Constant.INVALID_TASK_ID
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: Int? = INVALID_TASK_ID,
    var titulo: String = " ",
    var descricao: String = " ",
    var dataLimite: String = ""
) : Parcelable
