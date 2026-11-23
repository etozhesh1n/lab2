package ru.gr05405.math.polynomial.interpolating

interface Interpolated {

    /**
     * Вычисление значения интерполянта в точке x
     * @param x узел, в котором нужно вычислить значение интерполянта
     */
    operator fun invoke(x: Double): Double
}
