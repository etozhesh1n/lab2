package ru.gr05405.painting.convertation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * Тесты всех методов класса Converter:
 * xCrtToScr, xScrToCrt, yCrtToScr, yScrToCrt.
 */
class ConverterTest {

    @Test
    @DisplayName("Левая граница отрезка по X переводится в 0 экранной оси")
    fun xMinMapsToZero() {
        val plane = Plane(-5.0, 5.0, -5.0, 5.0, 800, 600)
        assertEquals(0f, Converter.xCrtToScr(plane.xMin, plane), 1e-3f)
    }

    @Test
    @DisplayName("Правая граница отрезка по X переводится в ширину области вывода")
    fun xMaxMapsToWidth() {
        val plane = Plane(-5.0, 5.0, -5.0, 5.0, 800, 600)
        assertEquals(800f, Converter.xCrtToScr(plane.xMax, plane), 1e-3f)
    }

    @Test
    @DisplayName("Середина отрезка по X переводится в середину области вывода")
    fun xMiddleMapsToHalfWidth() {
        val plane = Plane(-5.0, 5.0, -5.0, 5.0, 800, 600)
        assertEquals(400f, Converter.xCrtToScr(0.0, plane), 1e-3f)
    }

    @Test
    @DisplayName("Верхняя граница отрезка по Y переводится в 0 экранной оси")
    fun yMaxMapsToZero() {
        val plane = Plane(-5.0, 5.0, -5.0, 5.0, 800, 600)
        assertEquals(0f, Converter.yCrtToScr(plane.yMax, plane), 1e-3f)
    }

    @Test
    @DisplayName("Нижняя граница отрезка по Y переводится в высоту области вывода")
    fun yMinMapsToHeight() {
        val plane = Plane(-5.0, 5.0, -5.0, 5.0, 800, 600)
        assertEquals(600f, Converter.yCrtToScr(plane.yMin, plane), 1e-3f)
    }

    @Test
    @DisplayName("Середина отрезка по Y переводится в середину области вывода")
    fun yMiddleMapsToHalfHeight() {
        val plane = Plane(-5.0, 5.0, -5.0, 5.0, 800, 600)
        assertEquals(300f, Converter.yCrtToScr(0.0, plane), 1e-3f)
    }

    @ParameterizedTest
    @MethodSource("planeProvider")
    @DisplayName("Нулевая экранная координата X соответствует левой границе отрезка")
    fun xScrToCrtAtZero(plane: Plane) {
        assertEquals(plane.xMin, Converter.xScrToCrt(0f, plane), 1e-9)
    }

    @ParameterizedTest
    @MethodSource("planeProvider")
    @DisplayName("Правая граница области вывода соответствует правой границе отрезка")
    fun xScrToCrtAtWidth(plane: Plane) {
        assertEquals(plane.xMax, Converter.xScrToCrt(plane.width.toFloat(), plane), 1e-6)
    }

    @ParameterizedTest
    @MethodSource("planeProvider")
    @DisplayName("Нулевая экранная координата Y соответствует верхней границе отрезка")
    fun yScrToCrtAtZero(plane: Plane) {
        assertEquals(plane.yMax, Converter.yScrToCrt(0f, plane), 1e-9)
    }

    @ParameterizedTest
    @MethodSource("planeProvider")
    @DisplayName("Нижняя граница области вывода соответствует нижней границе отрезка")
    fun yScrToCrtAtHeight(plane: Plane) {
        assertEquals(plane.yMin, Converter.yScrToCrt(plane.height.toFloat(), plane), 1e-6)
    }

    @ParameterizedTest
    @MethodSource("planeProvider")
    @DisplayName("Преобразование X туда и обратно не меняет координату")
    fun xRoundTrip(plane: Plane) {
        for (i in 0..10) {
            val x = plane.xMin + (plane.xMax - plane.xMin) * i / 10.0
            val screenX = Converter.xCrtToScr(x, plane)
            val back = Converter.xScrToCrt(screenX, plane)
            assertEquals(x, back, 1e-4)
        }
    }

    @ParameterizedTest
    @MethodSource("planeProvider")
    @DisplayName("Преобразование Y туда и обратно не меняет координату")
    fun yRoundTrip(plane: Plane) {
        for (i in 0..10) {
            val y = plane.yMin + (plane.yMax - plane.yMin) * i / 10.0
            val screenY = Converter.yCrtToScr(y, plane)
            val back = Converter.yScrToCrt(screenY, plane)
            assertEquals(y, back, 1e-4)
        }
    }

    @ParameterizedTest
    @MethodSource("planeProvider")
    @DisplayName("Экранная координата X изменяется линейно")
    fun xIsLinear(plane: Plane) {
        val quarter = plane.xMin + (plane.xMax - plane.xMin) * 0.25
        val half = plane.xMin + (plane.xMax - plane.xMin) * 0.5
        val screenQuarter = Converter.xCrtToScr(quarter, plane)
        val screenHalf = Converter.xCrtToScr(half, plane)
        assertEquals(plane.width / 4f, screenQuarter, 1e-3f)
        assertEquals(plane.width / 2f, screenHalf, 1e-3f)
    }

    @Test
    @DisplayName("Экранная ось Y направлена вниз, а декартова - вверх")
    fun yAxisIsInverted() {
        val plane = Plane(-5.0, 5.0, -5.0, 5.0, 800, 600)
        val top = Converter.yCrtToScr(4.0, plane)
        val bottom = Converter.yCrtToScr(-4.0, plane)
        assertTrue(top < bottom) { "Большему значению Y должна соответствовать меньшая экранная координата" }
    }

    @Test
    @DisplayName("При увеличении области вывода экранная координата увеличивается пропорционально")
    fun screenCoordinateDependsOnScale() {
        val smallPlane = Plane(-5.0, 5.0, -5.0, 5.0, 400, 300)
        val largePlane = Plane(-5.0, 5.0, -5.0, 5.0, 800, 600)
        assertEquals(
            Converter.xCrtToScr(2.5, smallPlane) * 2,
            Converter.xCrtToScr(2.5, largePlane),
            1e-3f,
        )
        assertEquals(
            Converter.yCrtToScr(2.5, smallPlane) * 2,
            Converter.yCrtToScr(2.5, largePlane),
            1e-3f,
        )
    }

    companion object {
        @JvmStatic
        fun planeProvider(): Stream<Plane> = Stream.of(
            Plane(-5.0, 5.0, -5.0, 5.0, 800, 600),
            Plane(-10.0, 2.0, -100.0, 100.0, 1024, 768),
            Plane(0.0, 1.0, 0.0, 1.0, 100, 100),
            Plane(-100.0, 100.0, -100.0, 100.0, 400, 300),
        )
    }
}
