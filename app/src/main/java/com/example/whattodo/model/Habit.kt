package com.example.whattodo.model

data class Habit(
    val id: Int,
    val title : String,
    val reminder : Boolean,
    val reminderTime : String?
) {

}


