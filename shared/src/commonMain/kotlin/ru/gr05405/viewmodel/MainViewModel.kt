package ru.gr05405.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.gr05405.math.polynomial.Polynomial
import ru.gr05405.math.polynomial.interpolating.NewtonInterpolated
import ru.gr05405.math.polynomial.interpolating.Point
import ru.gr05405.painting.convertation.Plane
import kotlin.math.round


class MainViewModel {

    var plane by mutableStateOf(
        Plane(xMin = -5.0, xMax = 5.0, yMin = -6.5, yMax = 5.5)
    )
        private set

    var showPoints by mutableStateOf(true)
        private set

    var showFunction by mutableStateOf(true)
        private set

    var showDerivative by mutableStateOf(false)
        private set

    val points: List<Point> = DEFAULT_POINTS

    private val interpolated: NewtonInterpolated =
        NewtonInterpolated.fromPoints(*points.toTypedArray())

    val polynomial: Polynomial = interpolated.toPolynomial()

    val derivative: Polynomial = polynomial.derivative

    fun functionValue(x: Double): Double = interpolated(x)

    fun derivativeValue(x: Double): Double = derivative(x)


    fun onCanvasSizeChange(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        plane = plane.copy(width = width, height = height)
    }


    fun changeBound(type: ValueType, delta: Double) {
        setBound(type, boundValue(type) + delta)
    }


    fun setBound(type: ValueType, value: Double) {
        val newValue = roundToStep(value)
        plane = when (type) {
            ValueType.X_MIN -> plane.copy(xMin = newValue.coerceIn(MIN_BOUND, plane.xMax - MIN_GAP))
            ValueType.X_MAX -> plane.copy(xMax = newValue.coerceIn(plane.xMin + MIN_GAP, MAX_BOUND))
            ValueType.Y_MIN -> plane.copy(yMin = newValue.coerceIn(MIN_BOUND, plane.yMax - MIN_GAP))
            ValueType.Y_MAX -> plane.copy(yMax = newValue.coerceIn(plane.yMin + MIN_GAP, MAX_BOUND))
        }
    }

    fun onShowPointsChange(value: Boolean) {
        showPoints = value
    }

    fun onShowFunctionChange(value: Boolean) {
        showFunction = value
    }

    fun onShowDerivativeChange(value: Boolean) {
        showDerivative = value
    }

    private fun boundValue(type: ValueType): Double = when (type) {
        ValueType.X_MIN -> plane.xMin
        ValueType.X_MAX -> plane.xMax
        ValueType.Y_MIN -> plane.yMin
        ValueType.Y_MAX -> plane.yMax
    }

    private fun roundToStep(value: Double): Double = round(value / STEP) * STEP

    companion object {
        const val STEP = 0.1

        const val MIN_GAP = 0.1

        const val MIN_BOUND = -100.0

        const val MAX_BOUND = 100.0

        val DEFAULT_POINTS = listOf(
            Point(-4.5, 2.4),
            Point(-3.5, -0.6),
            Point(-2.0, -3.6),
            Point(-0.5, -1.6),
            Point(0.0, 3.0),
            Point(1.5, -0.1),
            Point(2.5, -3.6),
            Point(4.0, 0.6),
            Point(4.5, 2.4),
        )
    }
}
