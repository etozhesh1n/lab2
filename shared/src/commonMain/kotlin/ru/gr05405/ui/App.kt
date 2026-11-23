package ru.gr05405.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import ru.gr05405.painting.CartesianPainter
import ru.gr05405.painting.FunctionPainter
import ru.gr05405.painting.Painter
import ru.gr05405.painting.PointsPainter
import ru.gr05405.viewmodel.MainViewModel

@Composable
fun App(vm: MainViewModel) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
        ) {
            GraphArea(
                vm = vm,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            ControlPanel(
                vm = vm,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun GraphArea(vm: MainViewModel, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .padding(8.dp)
            .background(GraphColors.background)
            .border(1.dp, Color.Gray)
            .clipToBounds()
            .onSizeChanged { vm.onCanvasSizeChange(it.width, it.height) }
    ) {
        val plane = vm.plane

        val painters = mutableListOf<Painter>()
        if (vm.showFunction) {
            painters += FunctionPainter(function = vm::functionValue, color = GraphColors.function)
        }
        if (vm.showDerivative) {
            painters += FunctionPainter(
                function = vm::derivativeValue,
                color = GraphColors.derivative,
                strokeWidth = 1.5f,
            )
        }
        // система координат рисуется поверх графиков
        painters += CartesianPainter(
            textMeasurer = textMeasurer,
            axisColor = GraphColors.axis,
            tickColor = GraphColors.tick,
            labelColor = GraphColors.label,
        )
        if (vm.showPoints) {
            painters += PointsPainter(points = vm.points, color = GraphColors.points)
        }

        for (painter in painters) {
            with(painter) { paint(plane) }
        }
    }
}
