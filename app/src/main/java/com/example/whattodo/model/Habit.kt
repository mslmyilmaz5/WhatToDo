package com.example.whattodo.model

data class Habit(
    var id: Int,
    var title : String,
    var reminder : Boolean,
    var reminderTime : String?
) {

}


