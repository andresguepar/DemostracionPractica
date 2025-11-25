package com.example.demostracionpractica

import app.cash.turbine.test
import com.example.demostracionpractica.domain.Priority
import com.example.demostracionpractica.domain.Task
import com.example.demostracionpractica.domain.TaskRepository
import com.example.demostracionpractica.presentation.TaskUIState
import com.example.demostracionpractica.presentation.TaskViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private lateinit var repository: TaskRepository
    private lateinit var viewModel: TaskViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val sampleTask = Task(1, "Test Task", "Description", Priority.MEDIUM, false)

    @Before
    fun setup() {
        // Set main dispatcher for ViewModel's viewModelScope
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        val tasksFlow = MutableStateFlow(listOf(sampleTask))
        // getTasks is not a suspend function so use non-suspending mock
        every { repository.getTasks() } returns tasksFlow
        coEvery { repository.addTask(any()) } returns Unit
        coEvery { repository.updateTask(any()) } returns Unit
        coEvery { repository.deleteTask(any()) } returns Unit
        viewModel = TaskViewModel(repository)
    }

    @Test
    fun `uiState emits Success when tasks are received`() = runTest {
        // Ensure the viewModel's collection work is executed on the Main test dispatcher
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            // first item may be Loading, the Success update will arrive next
            var state = awaitItem()
            if (state is TaskUIState.Loading) state = awaitItem()
            assert(state is TaskUIState.Success)
            val tasks = (state as TaskUIState.Success).tasks
            assertEquals(1, tasks.size)
            assertEquals(sampleTask, tasks.first())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addTask calls repository if task is valid`() = runTest {
        viewModel.addTask(sampleTask)
        // advance the main dispatcher so viewModelScope coroutines run
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.addTask(sampleTask) }
    }

    @Test
    fun `addTask sets Error state if task is invalid`() = runTest {
        val invalidTask = sampleTask.copy(title = "")
        viewModel.addTask(invalidTask)
        val state = viewModel.uiState.value
        assert(state is TaskUIState.Error)
    }

    @Test
    fun `updateTask calls repository if task is valid`() = runTest {
        viewModel.updateTask(sampleTask)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.updateTask(sampleTask) }
    }

    @Test
    fun `deleteTask calls repository to delete`() = runTest {
        viewModel.deleteTask(sampleTask)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.deleteTask(sampleTask) }
    }

}

    @org.junit.After
    fun tearDown() {
        Dispatchers.resetMain()
    }
