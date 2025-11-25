package com.example.demostracionpractica.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.platform.testTag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demostracionpractica.domain.Task

/**
 * Composable function that renders the Task screen.
 * It shows different UI based on the TaskUIState.
 *
 * @param viewModel The TaskViewModel providing data and actions.
 * @param modifier Modifier to be applied to the layout.
 */
@Composable
fun TaskScreen(viewModel: TaskViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is TaskUIState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.testTag("loadingIndicator"))
            }
        }
        is TaskUIState.Error -> {
            val message = (uiState as TaskUIState.Error).message
                Box(modifier = modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text(text = message, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("errorMessage"))
            }
        }
        is TaskUIState.Success -> {
            val tasks = (uiState as TaskUIState.Success).tasks

            Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
                TaskInputField(
                    onAddTask = { viewModel.addTask(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.testTag("taskList")) {
                    items(tasks) { task ->
                        TaskItem(task = task, onToggleComplete = {
                            viewModel.updateTask(task.toggleCompletion())
                        }, onDelete = {
                            viewModel.deleteTask(task)
                        })
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete() },
                modifier = Modifier.testTag("checkbox_${task.id}")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                task.description?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        IconButton(onClick = onDelete, modifier = Modifier.testTag("deleteButton_${task.id}")) {
            // Keep icon simple so tests don't require material-icons dependency
            Text("Del")
        }
    }
}
