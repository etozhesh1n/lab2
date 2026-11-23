package ru.gr05405.painting

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import ru.gr05405.math.polynomial.interpolating.Point
import ru.gr05405.painting.convertation.Converter
import ru.gr05405.painting.convertation.Plane

/**
 * Отображение узлов интерполяции - точек, через которые проходит
 * интерполяционный полином Ньютона.
 */
class PointsPainter(
    private val points: List<Point>,
    private val color: Color = Color.Green,
    private val radius: Float = 4f,
) : Painter {

    override fun DrawScope.paint(plane: Plane) {
        if (plane.width <= 0 || plane.height <= 0) return

        for (point in points) {
            val x = Converter.xCrtToScr(point.x, plane)
            val y = Converter.yCrtToScr(point.y, plane)
            // точки за пределами области вывода не рисуются
            if (x < -radius || x > size.width + radius) continue
            if (y < -radius || y > size.height + radius) continue
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(x, y),
            )
        }
    }
}
