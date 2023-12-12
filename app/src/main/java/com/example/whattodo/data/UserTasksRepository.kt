package com.example.whattodo.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.whattodo.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException


class UserTasksRepository (

    private val dataStore: DataStore<Preferences>

){
    private companion object {
        val ID = intPreferencesKey("id")
        val TITLE = stringPreferencesKey("title")
        val IS_DONE = booleanPreferencesKey("is_done")
        val REMINDER = booleanPreferencesKey("reminder")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
        val PHOTO = booleanPreferencesKey("photo")
        val NOTIFICATION_ID = intPreferencesKey("notification_id")
        const val TAG = "UserTasksRepo"
    }


    val taskObject: Flow<Task> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val id = preferences[ID] ?: 0 // Varsayılan bir değer kullanılabilir
            val title = preferences[TITLE] ?: ""
            val isDone = preferences[IS_DONE] ?: false
            val reminder = preferences[REMINDER] ?: false
            val reminderTime = preferences[REMINDER_TIME] ?: null // Burada nullable String kullanılabilir
            val photo = preferences[PHOTO] ?: false
            val notificationId = preferences[NOTIFICATION_ID] ?: 0 // Varsayılan bir değer kullanılabilir

            Task(id, title, isDone, reminder, reminderTime, photo, notificationId)
        }

    suspend fun saveTask(task: Task){
        dataStore.edit {  preferences ->
            preferences[ID] = task.id
            preferences[TITLE] = task.title
            preferences[IS_DONE] = task.isDone
            preferences[REMINDER] = task.reminder
            preferences[REMINDER_TIME] = task.reminderTime ?: "null"
            preferences[PHOTO] = task.photo
            preferences[NOTIFICATION_ID] = task.notificationId
        }

        Log.d("Saved Task", "Task saved: $task")
    }




}