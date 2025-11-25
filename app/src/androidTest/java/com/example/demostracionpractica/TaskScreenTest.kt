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


// Tests instrumentados de UI para `TaskScreen` (Compose).
// - Ejecutan composables en un entorno de prueba (`createComposeRule`) y comprueban la
//   interfaz y la interacción con un ViewModel falso.
// - Son pruebas de UI/instrumentación (corren bajo `androidTest`) y verifican la
//   presentación y las llamadas al ViewModel, no la lógica del repositorio real.
@RunWith(AndroidJUnit4::class)
class TaskScreenTest {

    // Rule de Compose para tests de UI. Permite setContent y operaciones en nodos.
    @get:Rule
    val composeTestRule = createComposeRule()

    // ViewModel falso que usaremos para controlar el estado UI y capturar llamadas.
    private lateinit var viewModel: FakeTaskViewModel

    @Before
    fun setUp() {
        // Inicializamos el ViewModel falso antes de cada test para partir de un estado limpio.
        viewModel = FakeTaskViewModel()
    }

    @Test
    // Test UI: cuando el estado es Loading debe mostrarse un indicador de progreso.
    // Tipo: UI (instrumented)
    fun loadingStateDisplaysProgressIndicator() {
        viewModel.setState(TaskUIState.Loading)
        composeTestRule.setContent {
            TaskScreen(viewModel)
        }
        composeTestRule.onNodeWithTag("loadingIndicator").assertIsDisplayed()
    }

    @Test
    // Test UI: cuando el estado es Error debe mostrarse el mensaje de error correspondiente.
    // Verificamos también que el texto mostrado coincide con el mensaje enviado.
    // Tipo: UI (instrumented)
    fun errorStateDisplaysErrorMessage() {
        val errorMessage = "Error loading tasks"
        viewModel.setState(TaskUIState.Error(errorMessage))
        composeTestRule.setContent {
            TaskScreen(viewModel)
        }
        composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed().assertTextEquals(errorMessage)
    }

    @Test
    // Test UI: cuando el estado es Success con tareas, la lista y los controles por tarea
    // (checkbox, botón eliminar) deben existir para cada task.
    // Tipo: UI (instrumented)
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
            // Comprobamos existencia de nodos identificados por tags dinámicos.
            composeTestRule.onNodeWithTag("checkbox_${task.id}").assertExists()
            composeTestRule.onNodeWithTag("deleteButton_${task.id}").assertExists()
        }
    }

    @Test
    // Test UI: simula la creación de una nueva tarea mediante inputs y el botón "add".
    // Se verifica que el ViewModel haya recibido la llamada `addTask` con los datos correctos.
    // Tipo: UI + verificación de interacción con ViewModel (instrumented)
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

    // Fake ViewModel usado en los tests de UI.
    // - Hereda de TaskViewModel pero sustituye el flujo `uiState` por un MutableStateFlow.
    // - Captura llamadas a addTask para permitir aserciones sobre la interacción UI->VM.
    private class FakeTaskViewModel : TaskViewModel(MockTaskRepository()) {
        private val stateFlow = MutableStateFlow<TaskUIState>(TaskUIState.Loading)
        override val uiState = stateFlow

        var addedTaskCalled = false
        var addedTask: Task? = null

        // Permite establecer el estado UI de forma síncrona dentro del test.
        fun setState(state: TaskUIState) {
            runBlocking {
                stateFlow.emit(state)
            }
        }

        override fun addTask(task: Task) {
            // Guardamos la tarea para que las pruebas puedan verificar que se llamó correctamente.
            addedTaskCalled = true
            addedTask = task
        }

        override fun updateTask(task: Task) {
            // No necesario para estos tests; implementado vacío.
        }

        override fun deleteTask(task: Task) {
            // No necesario para estos tests; implementado vacío.
        }
    }

    // Mock simple del repositorio usado por el ViewModel falso.
    // - Retorna un MutableStateFlow vacío y no realiza operaciones reales de persistencia.
    // - Esto mantiene las pruebas como pruebas de UI/instrumentadas sin depender de DB o red.
    private class MockTaskRepository : com.example.demostracionpractica.domain.TaskRepository {
        override fun getTasks() = MutableStateFlow(emptyList<Task>())
        override suspend fun addTask(task: Task) {}
        override suspend fun updateTask(task: Task) {}
        override suspend fun deleteTask(task: Task) {}
    }
}
