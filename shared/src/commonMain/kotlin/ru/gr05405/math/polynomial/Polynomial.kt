package ru.gr05405.math.polynomial

import ru.gr05405.math.eq
import kotlin.math.absoluteValue
import kotlin.math.max


class Polynomial(coeff: List<Double>) {
    private val _coef: MutableList<Double> = filterCoef(coeff).toMutableList()

    val coef: List<Double>
        get() = _coef.toList()

    val degree: Int
        get() = _coef.size - 1

    val derivative: Polynomial
        get() = Polynomial(List(degree) { (it + 1) * _coef[it + 1] })

    constructor() : this(0.0)

    constructor(vararg coeff: Int) : this(coeff.map { it.toDouble() })

    constructor(p: Polynomial) : this(p.coef)

    constructor(vararg coeff: Double) : this(coeff.toList())

    override fun toString() = buildString {
        _coef.reversed().forEachIndexed { i, v ->
            if (v != 0.0 || degree == 0) {
                if (i != 0 || v < 0) append(if (v > 0) "+" else "-")
                if (v.absoluteValue != 1.0 || degree == i) {
                    append(v.absoluteValue)
                }
                if (degree != i) append("x")
                if (degree - i > 1) append("^${degree - i}")
            }
        }
    }

    operator fun plus(other: Polynomial) = Polynomial(
        List(max(_coef.size, other._coef.size)) {
            _coef.getOrElse(it) { 0.0 } + other._coef.getOrElse(it) { 0.0 }
        }
    )

    operator fun minus(other: Polynomial) = Polynomial(
        List(max(_coef.size, other._coef.size)) {
            _coef.getOrElse(it) { 0.0 } - other._coef.getOrElse(it) { 0.0 }
        }
    )

    operator fun times(k: Double) = Polynomial(_coef.map { it * k })

    operator fun times(other: Polynomial): Polynomial {
        val newCoef = MutableList(_coef.size + other._coef.size - 1) { 0.0 }
        for (i1 in _coef.indices) {
            for (i2 in other._coef.indices) {
                newCoef[i1 + i2] += _coef[i1] * other._coef[i2]
            }
        }
        return Polynomial(newCoef)
    }

    operator fun div(k: Double): Polynomial {
        require(k != 0.0) { "Попытка деления на ноль" }
        return Polynomial(_coef.map { it / k })
    }

    operator fun plusAssign(other: Polynomial) {
        val newCoeffs = List(max(_coef.size, other._coef.size)) {
            _coef.getOrElse(it) { 0.0 } + other._coef.getOrElse(it) { 0.0 }
        }
        _coef.clear()
        _coef.addAll(filterCoef(newCoeffs))
    }

    /**
     * Вычисление значения полинома в точке x по схеме Горнера
     */
    operator fun invoke(x: Double): Double {
        var result = 0.0
        for (i in _coef.indices.reversed()) {
            result = result * x + _coef[i]
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Polynomial) return false
        if (this === other) return true
        if (_coef.size != other._coef.size) return false
        return _coef.zip(other._coef).all { it.first eq it.second }
    }

    override fun hashCode(): Int =
        _coef.hashCode() + _coef.size.hashCode() * 31

    companion object {
        /**
         * Отбрасывание незначащих нулей в старших степенях полинома
         */
        private fun filterCoef(coefs: List<Double>): List<Double> {
            if (coefs.isEmpty()) return listOf(0.0)
            val last = coefs.indexOfLast { it != 0.0 }
            return if (last < 0) listOf(0.0) else coefs.subList(0, last + 1).toList()
        }
    }
}
