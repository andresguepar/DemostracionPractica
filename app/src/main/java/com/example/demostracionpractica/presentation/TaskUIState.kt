package com.example.demostracionpractica.presentation

import com.example.demostracionpractica.domain.Task

/**
 * Represents the UI state for the Task screen.
 */
sealed class TaskUIState {
    /**
     * Represents a loading state.
     */
    object Loading : TaskUIState()

    /**
     * Represents a success state with a list of tasks.
     * @property tasks The list of tasks to display.
     */
    data class Success(val tasks: List<Task>) : TaskUIState()

    /**
     * Represents an error state with a message.
     * @property message The error message.
     */
    data class Error(val message: String) : TaskUIState()
}
