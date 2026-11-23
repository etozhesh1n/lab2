package ru.gr05405.math.polynomial.interpolating

import ru.gr05405.math.eq
import ru.gr05405.math.polynomial.Polynomial


class NewtonInterpolated private constructor(vararg points: Point) : PolynomialInterpolated {

    private val nodes: DoubleArray = DoubleArray(points.size) { points[it].x }
    private val values: DoubleArray = DoubleArray(points.size) { points[it].y }

    private val dividedDifferences: DoubleArray = computeDividedDifferences()

    private fun computeDividedDifferences(): DoubleArray {
        val n = nodes.size
        val dd = DoubleArray(n)
        val table = Array(n) { DoubleArray(n) }


        for (i in 0 until n) {
            table[i][0] = values[i]
        }


        for (j in 1 until n) {
            for (i in 0 until n - j) {
                table[i][j] = (table[i + 1][j - 1] - table[i][j - 1]) / (nodes[i + j] - nodes[i])
            }
        }

        for (i in 0 until n) {
            dd[i] = table[0][i]
        }
        return dd
    }

    override fun toPolynomial(): Polynomial {
        val n = nodes.size
        var result = Polynomial(0.0)
        var basis = Polynomial(1.0)

        for (i in 0 until n) {
            result = result + (basis * dividedDifferences[i])
            if (i < n - 1) {
                basis = basis * Polynomial(-nodes[i], 1.0)
            }
        }
        return result
    }

    override val degree: Int
        get() = nodes.size - 1

    override operator fun invoke(x: Double): Double {

        nodes.indexOfFirst { it eq x }.also {
            if (it > -1) return values[it]
        }


        var result = dividedDifferences[nodes.size - 1]
        for (i in nodes.size - 2 downTo 0) {
            result = result * (x - nodes[i]) + dividedDifferences[i]
        }
        return result
    }

    companion object {
        fun fromPoints(vararg points: Point): NewtonInterpolated {
            if (points.isEmpty()) {
                throw IllegalArgumentException("Требуется хотя бы одна точка для построения интерполяционного полинома")
            }
            for (i in points.indices) {
                val xi = points[i].x
                for (j in i + 1 until points.size) {
                    val xj = points[j].x
                    if (xi eq xj) {
                        throw IllegalArgumentException("Узлы интерполяции не должны совпадать")
                    }
                }
            }
            return NewtonInterpolated(*points)
        }
    }
}
