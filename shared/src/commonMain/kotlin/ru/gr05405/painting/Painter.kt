package ru.gr05405.painting

import androidx.compose.ui.graphics.drawscope.DrawScope
import ru.gr05405.painting.convertation.Plane

/**
 * Общий интерфейс для всех объектов, которые умеют отображать
 * что-либо на графической области.
 */
interface Painter {
    fun DrawScope.paint(plane: Plane)
}
