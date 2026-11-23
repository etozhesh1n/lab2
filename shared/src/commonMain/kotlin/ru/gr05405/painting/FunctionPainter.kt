package ru.gr05405.painting

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import ru.gr05405.painting.convertation.Converter
import ru.gr05405.painting.convertation.Plane
import kotlin.math.abs


class FunctionPainter(
    private val function: (Double) -> Double,
    private val color: Color = Color.Blue,
    private val strokeWidth: Float = 2f,
) : Painter {

    override fun DrawScope.paint(plane: Plane) {
        if (plane.width <= 0 || plane.height <= 0) return

        val path = Path()
        var segmentStarted = false
        var previousY = 0f
        val topLimit = -2f * size.height
        val bottomLimit = 3f * size.height

        for (screenX in 0..plane.width) {
            val x = Converter.xScrToCrt(screenX.toFloat(), plane)
            val y = function(x)

            if (!y.isFinite()) {
                segmentStarted = false
                continue
            }

            val screenY = Converter.yCrtToScr(y, plane)
            if (screenY < topLimit || screenY > bottomLimit) {
                segmentStarted = false
                continue
            }

            if (!segmentStarted) {
                path.moveTo(screenX.toFloat(), screenY)
                segmentStarted = true
            } else if (abs(screenY - previousY) > size.height) {
                // резкий скачок значения - начинаем новый фрагмент графика
                path.moveTo(screenX.toFloat(), screenY)
            } else {
                path.lineTo(screenX.toFloat(), screenY)
            }
            previousY = screenY
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
    }
}
