package ru.gr05405.painting.convertation


data class Plane(
    val xMin: Double = -5.0,
    val xMax: Double = 5.0,
    val yMin: Double = -5.0,
    val yMax: Double = 5.0,
    val width: Int = 0,
    val height: Int = 0,
) { val xDen: Double
        get() = if (width <= 0 || xMax <= xMin) 0.0 else width / (xMax - xMin)
    val yDen: Double
        get() = if (height <= 0 || yMax <= yMin) 0.0 else height / (yMax - yMin)
}
