package ru.gr05405.viewmodel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MainViewModelTest {

    @Test
    @DisplayName("Начальные границы отображаемых отрезков")
    fun defaultBoundsTest() {
        val vm = MainViewModel()
        assertEquals(-5.0, vm.plane.xMin, 1e-9)
        assertEquals(5.0, vm.plane.xMax, 1e-9)
        assertEquals(-6.5, vm.plane.yMin, 1e-9)
        assertEquals(5.5, vm.plane.yMax, 1e-9)
    }

    @Test
    @DisplayName("Кнопки изменяют границы с шагом 0.1")
    fun changeBoundStepTest() {
        val vm = MainViewModel()
        vm.changeBound(ValueType.X_MIN, 0.1)
        assertEquals(-4.9, vm.plane.xMin, 1e-9)
        vm.changeBound(ValueType.X_MIN, -0.1)
        assertEquals(-5.0, vm.plane.xMin, 1e-9)
        vm.changeBound(ValueType.Y_MAX, 0.1)
        assertEquals(5.6, vm.plane.yMax, 1e-9)
    }

    @Test
    @DisplayName("Нижняя граница остаётся хотя бы на 0.1 меньше верхней")
    fun minGapTest() {
        val vm = MainViewModel()
        vm.setBound(ValueType.X_MIN, 5.0)
        assertEquals(4.9, vm.plane.xMin, 1e-9)
        vm.setBound(ValueType.Y_MAX, -6.5)
        assertEquals(-6.4, vm.plane.yMax, 1e-9)
    }

    @Test
    @DisplayName("Границы ограничены отрезком [-100; 100]")
    fun boundsLimitTest() {
        val vm = MainViewModel()
        vm.setBound(ValueType.X_MAX, 1000.0)
        assertEquals(100.0, vm.plane.xMax, 1e-9)
        vm.setBound(ValueType.X_MIN, -1000.0)
        assertEquals(-100.0, vm.plane.xMin, 1e-9)
        vm.changeBound(ValueType.Y_MAX, 1000.0)
        assertEquals(100.0, vm.plane.yMax, 1e-9)
    }

    @Test
    @DisplayName("Значение границы округляется до шага 0.1")
    fun roundingTest() {
        val vm = MainViewModel()
        vm.setBound(ValueType.X_MIN, -4.94)
        assertEquals(-4.9, vm.plane.xMin, 1e-9)
        vm.setBound(ValueType.X_MIN, -4.96)
        assertEquals(-5.0, vm.plane.xMin, 1e-9)
    }

    @Test
    @DisplayName("Галочки управляют отображаемыми элементами")
    fun togglesTest() {
        val vm = MainViewModel()
        assertTrue(vm.showPoints)
        assertTrue(vm.showFunction)
        assertFalse(vm.showDerivative)

        vm.onShowPointsChange(false)
        vm.onShowFunctionChange(false)
        vm.onShowDerivativeChange(true)

        assertFalse(vm.showPoints)
        assertFalse(vm.showFunction)
        assertTrue(vm.showDerivative)
    }

    @Test
    @DisplayName("Размер области вывода сохраняется только при положительных размерах")
    fun canvasSizeTest() {
        val vm = MainViewModel()
        vm.onCanvasSizeChange(0, 0)
        assertEquals(0, vm.plane.width)
        vm.onCanvasSizeChange(800, 600)
        assertEquals(800, vm.plane.width)
        assertEquals(600, vm.plane.height)
    }

    @Test
    @DisplayName("Полином проходит через все узлы интерполяции")
    fun polynomialPassesThroughPointsTest() {
        val vm = MainViewModel()
        for (point in vm.points) {
            assertEquals(point.y, vm.functionValue(point.x), 1e-9)
        }
    }
}
