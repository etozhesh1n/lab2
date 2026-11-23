package ru.gr05405.math.polynomial

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PolynomialTest {

    @Test
    @DisplayName("Получение строкового представления полинома равного 0.0")
    fun toStringZeroTest() {
        assertEquals("0.0", Polynomial().toString())
    }

    @ParameterizedTest
    @DisplayName("Проверка создания полиномов по набору коэффициентов")
    @MethodSource("coefficientsProvider")
    fun createPolynomialTest(coeffs: List<Double>, expectedCoefficients: List<Double>) {
        assertEquals(expectedCoefficients, Polynomial(coeffs).coef)
    }

    @ParameterizedTest
    @DisplayName("Преобразование полинома к строке для различных наборов коэффициентов")
    @MethodSource("coefficientsAndStringResultProvider")
    fun toStringParameterizedTest(coeffs: List<Double>, expected: String) {
        assertEquals(expected, Polynomial(coeffs).toString())
    }

    @ParameterizedTest
    @DisplayName("Сумма полиномов")
    @MethodSource("polynomialProviderForSum")
    fun polynomialSumTest(p1: Polynomial, p2: Polynomial, expected: Polynomial) {
        assertEquals(expected, p1 + p2)
    }

    @ParameterizedTest
    @DisplayName("Деление полинома на число")
    @MethodSource("polynomialAndNumberProviderForDiv")
    fun polynomialDivDoubleTest(p1: Polynomial, x: Double, expected: Polynomial?) {
        if (expected == null) {
            assertThrows(IllegalArgumentException::class.java) { p1 / x }
        } else {
            assertEquals(expected, p1 / x)
        }
    }

    @Test
    @DisplayName("Умножение полиномов")
    fun polynomialMultiplicationTest() {
        val p1 = Polynomial(1.0, 2.0)   // 1 + 2x
        val p2 = Polynomial(3.0, 1.0)   // 3 + x
        // (1 + 2x)(3 + x) = 3 + 7x + 2x^2
        assertEquals(Polynomial(3.0, 7.0, 2.0), p1 * p2)
    }

    @Test
    @DisplayName("Производная полинома")
    fun polynomialDerivativeTest() {
        // 2 + 3x + 4x^2 -> 3 + 8x
        val polynomial = Polynomial(2.0, 3.0, 4.0)
        assertEquals(Polynomial(3.0, 8.0), polynomial.derivative)
        // производная константы равна нулю
        assertEquals(Polynomial(0.0), Polynomial(5.0).derivative)
    }

    @Test
    @DisplayName("Вычисление значения полинома в точке")
    fun polynomialInvokeTest() {
        val polynomial = Polynomial(1.0, -2.0, 3.0) // 1 - 2x + 3x^2
        assertEquals(1.0, polynomial(0.0), 1e-9)
        assertEquals(2.0, polynomial(1.0), 1e-9)
        assertEquals(9.0, polynomial(2.0), 1e-9)
    }

    companion object {
        @JvmStatic
        fun coefficientsAndStringResultProvider() = Stream.of(
            Arguments.of(listOf(1.0), "1.0"),
            Arguments.of(listOf(-1.0), "-1.0"),
            Arguments.of(listOf(0.0, 1.0), "x"),
            Arguments.of(listOf(0.0, -1.0), "-x"),
            Arguments.of(listOf(1.0, 1.0), "x+1.0"),
            Arguments.of(listOf(-1.0, 1.0), "x-1.0"),
            Arguments.of(listOf(2.0, 1.0), "x+2.0"),
            Arguments.of(listOf(-2.0, 1.0), "x-2.0"),
            Arguments.of(listOf(0.0, 2.0), "2.0x"),
            Arguments.of(listOf(0.0, -2.0), "-2.0x"),
            Arguments.of(listOf(0.0, 0.0, 2.0), "2.0x^2"),
            Arguments.of(listOf(0.0, 0.0, -2.0), "-2.0x^2"),
            Arguments.of(listOf(0.0, 1.0, 2.0), "2.0x^2+x"),
            Arguments.of(listOf(0.0, -1.0, 2.0), "2.0x^2-x"),
            Arguments.of(listOf(1.0, 0.0, 2.0), "2.0x^2+1.0"),
            Arguments.of(listOf(-1.0, 0.0, 2.0), "2.0x^2-1.0"),
        )

        @JvmStatic
        fun coefficientsProvider() = Stream.of(
            Arguments.of(listOf(1.0), listOf(1.0)),
            Arguments.of(listOf(1.0, 2.0), listOf(1.0, 2.0)),
            Arguments.of(listOf(0.0, 2.0), listOf(0.0, 2.0)),
            Arguments.of(listOf(1.0, 0.0), listOf(1.0)),
            Arguments.of(listOf(0.0), listOf(0.0)),
            Arguments.of(listOf(0.0, 0.0, 0.0), listOf(0.0)),
            Arguments.of(listOf<Double>(), listOf(0.0)),
        )

        @JvmStatic
        fun polynomialProviderForSum() = Stream.of(
            Arguments.of(Polynomial(1.0), Polynomial(1.0), Polynomial(2.0)),
            Arguments.of(Polynomial(1, 3, 4, 6), Polynomial(-1, -3, -4, -6), Polynomial()),
            Arguments.of(Polynomial(1, 0, 4, 3), Polynomial(2, 3, 5, 5, 4), Polynomial(3, 3, 9, 8, 4)),
            Arguments.of(Polynomial(1, 3, 5, 4), Polynomial(2, 3, 5), Polynomial(3, 6, 10, 4)),
        )

        @JvmStatic
        fun polynomialAndNumberProviderForDiv() = Stream.of(
            Arguments.of(Polynomial(1.0), 1.0, Polynomial(1.0)),
            Arguments.of(Polynomial(3.0, 6.0), 3.0, Polynomial(1.0, 2.0)),
            Arguments.of(Polynomial(4.0), 0.0, null),
        )
    }
}
