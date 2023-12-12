package com.example.whattodo

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


private const val TASK_PROPERTIES_NAME = "task_properties"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = TASK_PROPERTIES_NAME
)

