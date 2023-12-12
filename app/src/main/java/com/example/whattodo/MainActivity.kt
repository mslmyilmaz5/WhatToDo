@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.whattodo



import TaskDBHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.whattodo.ui.theme.WhatToDoTheme
import com.example.whattodo.ui.theme.WhatToDoAppTask
import com.example.whattodo.ui.theme.generateTaskList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


class MainActivity : ComponentActivity() {



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
                    color = MaterialTheme.colorScheme.background
                ) {
                    setNotifications() // bu gercekten lazım mı bakmak lazım !!
                    val taskDBHelper = TaskDBHelper(this)
                    WhatToDoAppTask(taskDBHelper,whatToDoNotificationService)
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




