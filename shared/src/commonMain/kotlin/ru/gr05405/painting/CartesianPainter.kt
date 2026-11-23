package ru.gr05405.painting

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.sp
import ru.gr05405.painting.convertation.Converter
import ru.gr05405.painting.convertation.Plane
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round


class CartesianPainter(
    private val textMeasurer: TextMeasurer,
    private val axisColor: Color = Color.Black,
    private val tickColor: Color = Color.Blue,
    private val labelColor: Color = Color.Red,
    private val minTickDistance: Float = 6f,
    private val minLabelDistance: Float = 25f,
) : Painter {

    override fun DrawScope.paint(plane: Plane) {
        if (plane.width <= 0 || plane.height <= 0) return

        val x0 = Converter.xCrtToScr(0.0, plane)
        val y0 = Converter.yCrtToScr(0.0, plane)

        val xAxisVisible = y0 >= 0f && y0 <= size.height
        val yAxisVisible = x0 >= 0f && x0 <= size.width

        if (xAxisVisible) {
            paintXAxis(y0)
            paintXTicks(plane, y0, yAxisVisible)
        }
        if (yAxisVisible) {
            paintYAxis(x0)
            paintYTicks(plane, x0)
        }
    }

    private fun DrawScope.paintXAxis(y0: Float) {
        drawLine(axisColor, Offset(0f, y0), Offset(size.width - 6f, y0), strokeWidth = 1.2f)
        val arrow = Path().apply {
            moveTo(size.width, y0)
            lineTo(size.width - 8f, y0 - 4f)
            lineTo(size.width - 8f, y0 + 4f)
            close()
        }
        drawPath(arrow, axisColor)
    }

    private fun DrawScope.paintYAxis(x0: Float) {
        drawLine(axisColor, Offset(x0, 6f), Offset(x0, size.height), strokeWidth = 1.2f)
        val arrow = Path().apply {
            moveTo(x0, 0f)
            lineTo(x0 - 4f, 8f)
            lineTo(x0 + 4f, 8f)
            close()
        }
        drawPath(arrow, axisColor)
    }

    private fun DrawScope.paintXTicks(plane: Plane, y0: Float, yAxisVisible: Boolean) {
        val step = tickStep(plane.xDen)
        val major = majorStep(step, plane.xDen)
        val first = ceil(plane.xMin / step).toInt()
        val last = floor(plane.xMax / step).toInt()

        for (i in first..last) {
            val value = i * step
            val x = Converter.xCrtToScr(value, plane)
            val tick = tickStyle(value, step, major)
            drawLine(
                color = tick.color,
                start = Offset(x, y0 - tick.length),
                end = Offset(x, y0 + tick.length),
                strokeWidth = tick.width,
            )
            //
            val skipOrigin = yAxisVisible && abs(value) < step / 2
            if (tick.major && !skipOrigin) {
                drawXLabel(value, x, y0 + tick.length + 2f)
            }
        }
    }

    private fun DrawScope.paintYTicks(plane: Plane, x0: Float) {
        val step = tickStep(plane.yDen)
        val major = majorStep(step, plane.yDen)
        val first = ceil(plane.yMin / step).toInt()
        val last = floor(plane.yMax / step).toInt()

        for (i in first..last) {
            val value = i * step
            val y = Converter.yCrtToScr(value, plane)
            val tick = tickStyle(value, step, major)
            drawLine(
                color = tick.color,
                start = Offset(x0 - tick.length, y),
                end = Offset(x0 + tick.length, y),
                strokeWidth = tick.width,
            )
            if (tick.major) {
                drawYLabel(value, x0 - tick.length - 3f, y)
            }
        }
    }

    private fun DrawScope.drawXLabel(value: Double, x: Float, top: Float) {
        val layout = textMeasurer.measure(formatTick(value), labelStyle)
        val maxLeft = (size.width - layout.size.width).coerceAtLeast(0f)
        val left = (x - layout.size.width / 2f).coerceIn(0f, maxLeft)
        drawText(textLayoutResult = layout, topLeft = Offset(left, top))
    }

    private fun DrawScope.drawYLabel(value: Double, right: Float, centerY: Float) {
        val layout = textMeasurer.measure(formatTick(value), labelStyle)
        val maxLeft = (size.width - layout.size.width).coerceAtLeast(0f)
        val left = (right - layout.size.width).coerceIn(0f, maxLeft)
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(left, centerY - layout.size.height / 2f),
        )
    }

    private val labelStyle = TextStyle(color = labelColor, fontSize = 10.sp)

    /**
     * Шаг штрихов с учётом масштаба: берётся минимальный шаг из "красивых"
     * значений, при котором расстояние между штрихами на экране не меньше
     * minTickDistance пикселей.
     */
    private fun tickStep(pixelsPerUnit: Double): Double {
        if (pixelsPerUnit <= 0.0) return TICK_STEPS.last()
        for (step in TICK_STEPS) {
            if (step * pixelsPerUnit >= minTickDistance) return step
        }
        return TICK_STEPS.last()
    }

    /**
     * Шаг подписей: подписывается каждый штрих, кратный этому значению.
     * Шаг подбирается так, чтобы подписи не накладывались друг на друга.
     */
    private fun majorStep(tickStep: Double, pixelsPerUnit: Double): Double {
        val base = tickStep.coerceAtLeast(1.0)
        for (factor in LABEL_FACTORS) {
            val candidate = base * factor
            if (candidate * pixelsPerUnit >= minLabelDistance) return candidate
        }
        return base * LABEL_FACTORS.last()
    }

    private fun tickStyle(value: Double, step: Double, majorStep: Double): TickStyle = when {
        isMultiple(value, majorStep) -> TickStyle(6f, 1.5f, tickColor, true)
        isMultiple(value, if (step <= 0.5) 0.5 else majorStep / 2) ->
            TickStyle(4f, 1.2f, Color(0xFF3366CC), false)
        else -> TickStyle(2f, 1f, Color(0xFF88AADD), false)
    }

    private data class TickStyle(
        val length: Float,
        val width: Float,
        val color: Color,
        val major: Boolean,
    )

    private companion object {
        val TICK_STEPS = doubleArrayOf(0.1, 0.2, 0.5, 1.0, 2.0, 5.0, 10.0, 20.0, 50.0, 100.0)
        val LABEL_FACTORS = doubleArrayOf(1.0, 2.0, 5.0, 10.0, 20.0, 50.0, 100.0)

        fun isMultiple(value: Double, step: Double): Boolean {
            if (step <= 0.0) return false
            val quotient = value / step
            return abs(quotient - round(quotient)) < 1e-6
        }

        fun formatTick(value: Double): String {
            val rounded = round(value * 10.0) / 10.0
            if (abs(rounded) < 1e-9) return "0.0"
            val integer = rounded.toInt()
            return if (rounded == integer.toDouble()) "$integer.0" else rounded.toString()
        }
    }
}
