package ru.gr05405.math

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.ulp

/**
 * Сравнение двух чисел с плавающей точкой с учётом погрешности.
 * Два числа считаются равными, если разница между ними меньше
 * десяти машинных эпсилон.
 */
infix fun Double.eq(other: Double) =
    (this - other).absoluteValue < max(ulp, other.ulp) * 10.0

infix fun Double.neq(other: Double) = !(this eq other)
