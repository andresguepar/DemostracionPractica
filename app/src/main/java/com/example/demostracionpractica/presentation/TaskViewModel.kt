package com.example.demostracionpractica.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demostracionpractica.domain.Task
import com.example.demostracionpractica.domain.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for orchestrating data flow between the TaskRepository and UI,
 * managing validations and transformations.
 *
 * @property taskRepository The repository providing task data.
 */
open class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<TaskUIState>(TaskUIState.Loading)
    open val uiState: StateFlow<TaskUIState> = _uiState

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            taskRepository.getTasks().collectLatest { tasks ->
                _uiState.value = TaskUIState.Success(tasks)
            }
        }
    }

    /**
     * Attempts to add a new task.
     * Validates before adding.
     *
     * @param task The task to add.
     */
    open fun addTask(task: Task) {
        if (!task.isValid()) {
            _uiState.value = TaskUIState.Error("Task is not valid")
            return
        }
        viewModelScope.launch {
            taskRepository.addTask(task)
        }
    }

    /**
     * Attempts to update an existing task.
     * Validates before updating.
     *
     * @param task The task to update.
     */
    open fun updateTask(task: Task) {
        if (!task.isValid()) {
            _uiState.value = TaskUIState.Error("Task is not valid")
            return
        }
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    /**
     * Deletes a task.
     *
     * @param task The task to delete.
     */
    open fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}
