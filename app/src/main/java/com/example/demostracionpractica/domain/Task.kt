package com.example.demostracionpractica.domain

/**
 * Represents a Task with a title, description, priority, and completion status.
 * This is an immutable data class including business logic methods.
 *
 * @property id Unique identifier for the task
 * @property title Title of the task
 * @property description Optional detailed description
 * @property priority Priority level of the task
 * @property isCompleted Whether the task is marked as completed
 */
data class Task(
    val id: Int,
    val title: String,
    val description: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false
) {
    /**
     * Checks if the task data is valid.
     * Title must be non-empty and max length 100 characters.
     * Description max length 500 characters if present.
     */
    fun isValid(): Boolean {
        if (title.isBlank() || title.length > 100) return false
        if (description != null && description.length > 500) return false
        return true
    }

    /**
     * Returns a new Task with the completion status toggled.
     */
    fun toggleCompletion(): Task =
        this.copy(isCompleted = !this.isCompleted)
}
