package com.example.demostracionpractica.data

import com.example.demostracionpractica.domain.Task
import com.example.demostracionpractica.domain.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementation of [TaskRepository] using a MutableStateFlow for reactive data.
 * Includes validations and ordering of tasks.
 * This simulates persistence; in a real app, use Room or Firestore.
 */
class TaskRepositoryImpl : TaskRepository {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    override fun getTasks(): Flow<List<Task>> = _tasks.map { tasks ->
        // Return tasks ordered by priority (HIGH -> LOW) and then by title
        tasks.sortedWith(compareByDescending<Task> { it.priority }
            .thenBy { it.title })
    }

    // Mutex for thread safety on mutations
    private val mutex = Mutex()

    override suspend fun addTask(task: Task) {
        if (!task.isValid()) throw IllegalArgumentException("Invalid task")
        mutex.withLock {
            _tasks.value = _tasks.value + task
        }
    }

    override suspend fun updateTask(task: Task) {
        if (!task.isValid()) throw IllegalArgumentException("Invalid task")
        mutex.withLock {
            _tasks.value = _tasks.value.map { if (it.id == task.id) task else it }
        }
    }

    override suspend fun deleteTask(task: Task) {
        mutex.withLock {
            _tasks.value = _tasks.value.filterNot { it.id == task.id }
        }
    }
}
