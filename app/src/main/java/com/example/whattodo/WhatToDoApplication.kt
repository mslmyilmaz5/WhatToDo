package com.example.whattodo

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.whattodo.data.UserTasksRepository


private const val TASK_PROPERTIES_NAME = "task_properties"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = TASK_PROPERTIES_NAME
)

class WhatToDoApplication: Application() {

    lateinit var userTasksRepository: UserTasksRepository

    override fun onCreate() {
        super.onCreate()
        userTasksRepository = UserTasksRepository(dataStore)
    }

}