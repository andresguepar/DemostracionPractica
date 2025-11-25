package com.example.demostracionpractica.domain

import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for Task data operations.
 */
interface TaskRepository {
    /**
     * Returns a flow of the current list of tasks.
     */
    fun getTasks(): Flow<List<Task>>

    /**
     * Adds a new task.
     *
     * @param task The task to add.
     */
    suspend fun addTask(task: Task)

    /**
     * Updates an existing task.
     *
     * @param task The task with updated data.
     */
    suspend fun updateTask(task: Task)

    /**
     * Deletes a task.
     *
     * @param task The task to delete.
     */
    suspend fun deleteTask(task: Task)
}
