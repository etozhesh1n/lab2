package ru.gr05405.math.polynomial.interpolating

import ru.gr05405.math.polynomial.Polynomial

interface PolynomialInterpolated : Interpolated {


    fun toPolynomial(): Polynomial


    val degree: Int
}
