package com.example.whattodo.model

data class Task(
    var id: Int,
    var title: String,
    var isDone: Boolean,
    var reminder: Boolean,
    var reminderTime: String?,
    var photo: Boolean,
    var notificationId: Int
)



