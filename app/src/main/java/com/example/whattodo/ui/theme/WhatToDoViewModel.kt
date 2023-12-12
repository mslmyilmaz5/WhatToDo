package com.example.whattodo.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.whattodo.WhatToDoApplication
import com.example.whattodo.data.UserTasksRepository
import com.example.whattodo.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WhatToDoViewModel (

    private val userTasksRepository: UserTasksRepository

): ViewModel() {

    val taskObject = userTasksRepository.taskObject
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)



    fun saveTask(task: Task) {
       viewModelScope.launch {
           userTasksRepository.saveTask(task)
       }

   }

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WhatToDoApplication)
                WhatToDoViewModel(application.userTasksRepository)
            }
        }
    }
}

