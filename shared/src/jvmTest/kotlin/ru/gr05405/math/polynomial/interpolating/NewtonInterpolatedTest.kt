package ru.gr05405.math.polynomial.interpolating

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.gr05405.math.polynomial.Polynomial

class NewtonInterpolatedTest {

    @Test
    @DisplayName("Полином проходит через все узлы интерполяции")
    fun interpolatesAllNodes() {
        val points = arrayOf(
            Point(-1.0, -1.0),
            Point(0.0, 0.0),
            Point(1.0, 1.0),
            Point(2.0, 8.0),
        )
        val polynomial = NewtonInterpolated.fromPoints(*points)
        for (point in points) {
            assertEquals(point.y, polynomial(point.x), 1e-9)
        }
    }

    @Test
    @DisplayName("Степень полинома на единицу меньше количества узлов")
    fun degreeTest() {
        val polynomial = NewtonInterpolated.fromPoints(
            Point(0.0, 1.0),
            Point(1.0, 2.0),
            Point(2.0, 5.0),
        )
        assertEquals(2, polynomial.degree)
    }

    @Test
    @DisplayName("Приведение полинома Ньютона к канонической форме")
    fun toPolynomialTest() {
        // узлы функции y = x^2
        val polynomial = NewtonInterpolated.fromPoints(
            Point(0.0, 0.0),
            Point(1.0, 1.0),
            Point(2.0, 4.0),
        ).toPolynomial()

        assertEquals(listOf(0.0, 0.0, 1.0), polynomial.coef)
        assertEquals(2, polynomial.degree)
    }

    @Test
    @DisplayName("Полином, построенный по точкам кубической функции, совпадает с x^3")
    fun cubicPolynomialTest() {
        val polynomial = NewtonInterpolated.fromPoints(
            Point(-1.0, -1.0),
            Point(0.0, 0.0),
            Point(1.0, 1.0),
            Point(2.0, 8.0),
        ).toPolynomial()

        assertEquals(listOf(0.0, 0.0, 0.0, 1.0), polynomial.coef)
    }

    @Test
    @DisplayName("Значение канонической формы совпадает со значением формы Ньютона")
    fun valueMatchesCanonicalPolynomialTest() {
        val interpolated = NewtonInterpolated.fromPoints(
            Point(-2.0, 3.0),
            Point(0.0, -1.0),
            Point(1.0, 4.0),
            Point(3.0, 2.0),
        )
        val polynomial: Polynomial = interpolated.toPolynomial()
        for (x in listOf(-1.5, -0.5, 0.5, 2.0, 2.5)) {
            assertEquals(polynomial(x), interpolated(x), 1e-9)
        }
    }

    @Test
    @DisplayName("Полином по одной точке является константой")
    fun singlePointTest() {
        val polynomial = NewtonInterpolated.fromPoints(Point(2.0, 5.0))
        assertEquals(0, polynomial.degree)
        assertEquals(5.0, polynomial(2.0), 1e-9)
        assertEquals(5.0, polynomial(100.0), 1e-9)
    }

    @Test
    @DisplayName("Пустой набор точек приводит к исключению")
    fun emptyPointsTest() {
        assertThrows(IllegalArgumentException::class.java) {
            NewtonInterpolated.fromPoints()
        }
    }

    @Test
    @DisplayName("Совпадающие узлы интерполяции приводят к исключению")
    fun duplicateNodesTest() {
        assertThrows(IllegalArgumentException::class.java) {
            NewtonInterpolated.fromPoints(
                Point(1.0, 2.0),
                Point(2.0, 3.0),
                Point(1.0, 4.0),
            )
        }
    }
}
