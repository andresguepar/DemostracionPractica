package com.example.demostracionpractica.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.platform.testTag
// Avoid using material icons dependency in tests; use a simple glyph instead
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demostracionpractica.domain.Priority
import com.example.demostracionpractica.domain.Task

/**
 * Composable for inputting a new Task with title, optional description, and priority dropdown.
 *
 * @param onAddTask Lambda called when task is ready to be added.
 * @param modifier Modifier applied to the root composable.
 */
@Composable
fun TaskInputField(
    onAddTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }

    Column(modifier = modifier.padding(8.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth().testTag("taskTitleInput")
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag("taskDescriptionInput")
        )
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.padding(top = 8.dp).testTag("priorityDropdownButton")
        ) {
            Text(text = "Priority: ${selectedPriority.name}")
            Text("▾")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Priority.values().forEach { priority ->
                DropdownMenuItem(
                    text = { Text(priority.name) },
                    onClick = {
                        selectedPriority = priority
                        expanded = false
                    },
                    modifier = Modifier.testTag("priorityOption_${priority.name}")
                )
            }
        }
        Button(
            onClick = {
                val task = Task(
                    id = (0..100000).random(),
                    title = title,
                    description = if (description.isBlank()) null else description,
                    priority = selectedPriority,
                    isCompleted = false
                )
                onAddTask(task)
                title = ""
                description = ""
                selectedPriority = Priority.MEDIUM
            },
            modifier = Modifier.padding(top = 8.dp).testTag("addTaskButton"),
            enabled = title.isNotBlank()
        ) {
            Text("Add Task")
        }
    }
}
