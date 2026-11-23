package ru.gr05405.painting.convertation

object Converter {

    fun xCrtToScr(x: Double, plane: Plane): Float =
        ((x - plane.xMin) * plane.xDen).toFloat()


    fun xScrToCrt(x: Float, plane: Plane): Double =
        x / plane.xDen + plane.xMin


    fun yCrtToScr(y: Double, plane: Plane): Float =
        ((plane.yMax - y) * plane.yDen).toFloat()

    fun yScrToCrt(y: Float, plane: Plane): Double =
        plane.yMax - y / plane.yDen
}
