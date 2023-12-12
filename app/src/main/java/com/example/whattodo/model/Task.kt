package com.example.whattodo.model


data class Task(
    val id: Int,
    val title: String,
    val isDone: Boolean,
    val reminder: Boolean,
    val reminderTime: String?,
    val photo: Boolean,
    val notificationId: Int
){

}



