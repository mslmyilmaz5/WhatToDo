@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.whattodo



import DatabaseHelper
import android.content.Context
import android.content.SharedPreferences

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.whattodo.model.Habit
import com.example.whattodo.model.Task

import com.example.whattodo.ui.theme.WhatToDoTheme
import com.example.whattodo.ui.theme.WhatToDoAppTask
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_KEY_TODAY_DATE = "today_date"

    private val whatToDoNotificationService by lazy {
        WhatToDoNotificationService(this,"")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatToDoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF4044C9)
                ) {
                    setNotifications() // bu gercekten lazım mı bakmak lazım !!
                    val databaseHelper = DatabaseHelper(this)
                    sharedPreferences = getSharedPreferences("WhatToDo_Prefs", Context.MODE_PRIVATE)
                    transferHabits(databaseHelper) //Transfers habits to task screen
                    WhatToDoAppTask(databaseHelper,whatToDoNotificationService)
                }
            }
        }
    }

    private fun transferHabits(databaseHelper : DatabaseHelper){
        val habits = databaseHelper.getAllHabits()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        for (habit in habits){
            if (!databaseHelper.hasTaskForDateAndHabitId(currentDate,habit.id)){
                val task = Task(0,habit.title,false,habit.reminder,habit.reminderTime,false,
                    Random.nextInt(),"",habit.id)
                databaseHelper.addTask(task)
                if(task.reminder){
                    whatToDoNotificationService.scheduleNotification(
                        task.title,
                        task.reminderTime,
                        task.notificationId
                    )
                }
            }
        }
    }
}


@Composable
fun setNotifications(){

        val postNotificationPermission =
            rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)

        val scheduleExactAlarmPermission =
            rememberPermissionState(permission = android.Manifest.permission.USE_EXACT_ALARM)


        LaunchedEffect(key1 = true) {
            if (!postNotificationPermission.status.isGranted) {
                postNotificationPermission.launchPermissionRequest()
            }

            if (!scheduleExactAlarmPermission.status.isGranted) {
                scheduleExactAlarmPermission.launchPermissionRequest()
            }

        }

    }



