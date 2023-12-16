package com.example.whattodo

import DatabaseHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import com.example.whattodo.ui.theme.WhatToDoAppHabit
import com.example.whattodo.ui.theme.WhatToDoTheme



class HabitsActivity : ComponentActivity() {

    private val whatToDoNotificationService by lazy {
        WhatToDoNotificationService(applicationContext,"")
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
                    val databaseHelper = DatabaseHelper(this)
                    WhatToDoAppHabit(databaseHelper,whatToDoNotificationService)
                }
            }
        }
    }
}
