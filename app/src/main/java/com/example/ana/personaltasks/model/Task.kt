package com.example.ana.personaltasks.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var titulo: String = " ",
    var descricao: String = " ",
    var dataLimite: String = ""
) : Parcelable
