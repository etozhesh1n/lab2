package ru.gr05405

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ru.gr05405.ui.App
import ru.gr05405.viewmodel.MainViewModel

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Построение графика полинома",
        state = rememberWindowState(size = DpSize(800.dp, 860.dp)),
    ) {
        val viewModel = remember { MainViewModel() }
        App(viewModel)
    }
}
