package com.example.demostracionpractica

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.demostracionpractica.domain.Priority
import com.example.demostracionpractica.domain.Task
import com.example.demostracionpractica.presentation.TaskScreen
import com.example.demostracionpractica.presentation.TaskViewModel
import com.example.demostracionpractica.presentation.TaskUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class TaskScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: FakeTaskViewModel

    @Before
    fun setUp() {
        viewModel = FakeTaskViewModel()
    }

    @Test
    fun loadingStateDisplaysProgressIndicator() {
        viewModel.setState(TaskUIState.Loading)
        composeTestRule.setContent {
            TaskScreen(viewModel)
        }
        composeTestRule.onNodeWithTag("loadingIndicator").assertIsDisplayed()
    }

    @Test
    fun errorStateDisplaysErrorMessage() {
        val errorMessage = "Error loading tasks"
        viewModel.setState(TaskUIState.Error(errorMessage))
        composeTestRule.setContent {
            TaskScreen(viewModel)
        }
        composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed().assertTextEquals(errorMessage)
    }

    @Test
    fun successStateDisplaysTasks() {
        val tasks = listOf(
            Task(1, "Task 1", "Description 1", Priority.HIGH, false),
            Task(2, "Task 2", null, Priority.LOW, true)
        )
        viewModel.setState(TaskUIState.Success(tasks))
        composeTestRule.setContent {
            TaskScreen(viewModel)
        }
        composeTestRule.onNodeWithTag("taskList").assertIsDisplayed()
        tasks.forEach { task ->
            composeTestRule.onNodeWithTag("checkbox_${task.id}").assertExists()
            composeTestRule.onNodeWithTag("deleteButton_${task.id}").assertExists()
        }
    }

    @Test
    fun clickingAddTaskButtonAddsTask() {
        viewModel.setState(TaskUIState.Success(emptyList()))
        composeTestRule.setContent {
            TaskScreen(viewModel)
        }

        // Input title and description
        composeTestRule.onNodeWithTag("taskTitleInput").performTextInput("New Task")
        composeTestRule.onNodeWithTag("taskDescriptionInput").performTextInput("New Description")
        composeTestRule.onNodeWithTag("priorityDropdownButton").performClick()
        composeTestRule.onNodeWithTag("priorityOption_HIGH").performClick()

        // Click add button
        composeTestRule.onNodeWithTag("addTaskButton").performClick()

        // Verify ViewModel received addTask call
        assertTrue(viewModel.addedTaskCalled)
        assert(viewModel.addedTask?.title == "New Task")
        assert(viewModel.addedTask?.description == "New Description")
        assert(viewModel.addedTask?.priority == Priority.HIGH)
    }

    private class FakeTaskViewModel : TaskViewModel(MockTaskRepository()) {
        private val stateFlow = MutableStateFlow<TaskUIState>(TaskUIState.Loading)
        override val uiState = stateFlow

        var addedTaskCalled = false
        var addedTask: Task? = null

        fun setState(state: TaskUIState) {
            runBlocking {
                stateFlow.emit(state)
            }
        }

        override fun addTask(task: Task) {
            addedTaskCalled = true
            addedTask = task
        }

        override fun updateTask(task: Task) {
        }

        override fun deleteTask(task: Task) {
        }
    }

    // Mock repository does nothing for simplicity
    private class MockTaskRepository : com.example.demostracionpractica.domain.TaskRepository {
        override fun getTasks() = MutableStateFlow(emptyList<Task>())
        override suspend fun addTask(task: Task) {}
        override suspend fun updateTask(task: Task) {}
        override suspend fun deleteTask(task: Task) {}
    }
}
