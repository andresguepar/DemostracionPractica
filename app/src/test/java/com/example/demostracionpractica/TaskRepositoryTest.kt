package com.example.demostracionpractica

import app.cash.turbine.test
import com.example.demostracionpractica.data.TaskRepositoryImpl
import com.example.demostracionpractica.domain.Priority
import com.example.demostracionpractica.domain.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskRepositoryTest {

    private lateinit var repository: TaskRepositoryImpl

    @Before
    fun setup() {
        repository = TaskRepositoryImpl()
    }

    @Test
    fun `addTask adds task correctly`() = runTest {
        val task = Task(1, "title", "desc", Priority.MEDIUM, false)
        repository.addTask(task)
        repository.getTasks().test {
            val tasks = awaitItem()
            assertTrue(tasks.contains(task))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `addTask throws exception for invalid task`() = runTest {
        val invalidTask = Task(2, "", "desc", Priority.LOW, false)
        repository.addTask(invalidTask)
    }

    @Test
    fun `updateTask updates existing task`() = runTest {
        val task = Task(3, "title3", "desc3", Priority.HIGH, false)
        repository.addTask(task)

        val updatedTask = task.copy(title = "new title", isCompleted = true)
        repository.updateTask(updatedTask)

        repository.getTasks().test {
            val list = awaitItem()
            assertTrue(list.any { it.title == "new title" && it.isCompleted })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `updateTask throws exception for invalid task`() = runTest {
        val task = Task(4, "title4", "desc4", Priority.MEDIUM, false)
        repository.addTask(task)
        val invalidUpdate = task.copy(title = "")
        repository.updateTask(invalidUpdate)
    }

    @Test
    fun `deleteTask removes task`() = runTest {
        val task = Task(5, "title5", "desc5", Priority.LOW, false)
        repository.addTask(task)

        repository.deleteTask(task)

        repository.getTasks().test {
            val list = awaitItem()
            assertFalse(list.contains(task))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTasks returns tasks sorted by priority then title`() = runTest {
        val task1 = Task(6, "A task", "desc", Priority.LOW, false)
        val task2 = Task(7, "B task", "desc", Priority.HIGH, false)
        val task3 = Task(8, "C task", "desc", Priority.MEDIUM, false)

        repository.addTask(task1)
        repository.addTask(task2)
        repository.addTask(task3)

        repository.getTasks().test {
            val list = awaitItem()
            assertEquals(list[0], task2)  // High priority first
            assertEquals(list[1], task3)  // Medium
            assertEquals(list[2], task1)  // Low
            cancelAndIgnoreRemainingEvents()
        }
    }
}
