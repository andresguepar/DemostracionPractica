package com.example.demostracionpractica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.demostracionpractica.ui.theme.DemostracionPracticaTheme
import com.example.demostracionpractica.presentation.TaskViewModelFactory
import com.example.demostracionpractica.data.TaskRepositoryImpl
import com.example.demostracionpractica.presentation.TaskViewModel
import com.example.demostracionpractica.presentation.TaskScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemostracionPracticaTheme {
                // Create the ViewModel using a simple factory so TaskScreen can receive it
                val viewModel: TaskViewModel by viewModels { TaskViewModelFactory(TaskRepositoryImpl()) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TaskScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DemostracionPracticaTheme {
        Greeting("Android")
    }
}