package ru.gr05405.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gr05405.viewmodel.MainViewModel
import ru.gr05405.viewmodel.ValueType
import kotlin.math.abs
import kotlin.math.round

@Composable
fun ControlPanel(vm: MainViewModel, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(8.dp)) {
        // границы отрезков
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BoundInput(
                    label = "Xmin:",
                    value = vm.plane.xMin,
                    onStep = { vm.changeBound(ValueType.X_MIN, it) },
                    onValueChange = { vm.setBound(ValueType.X_MIN, it) },
                    modifier = Modifier.weight(1f),
                )
                BoundInput(
                    label = "Xmax:",
                    value = vm.plane.xMax,
                    onStep = { vm.changeBound(ValueType.X_MAX, it) },
                    onValueChange = { vm.setBound(ValueType.X_MAX, it) },
                    modifier = Modifier.weight(1f),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BoundInput(
                    label = "Ymin:",
                    value = vm.plane.yMin,
                    onStep = { vm.changeBound(ValueType.Y_MIN, it) },
                    onValueChange = { vm.setBound(ValueType.Y_MIN, it) },
                    modifier = Modifier.weight(1f),
                )
                BoundInput(
                    label = "Ymax:",
                    value = vm.plane.yMax,
                    onStep = { vm.changeBound(ValueType.Y_MAX, it) },
                    onValueChange = { vm.setBound(ValueType.Y_MAX, it) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // отображаемые элементы
        Column(
            modifier = Modifier.padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            CheckRow(
                label = "Отображать точки",
                checked = vm.showPoints,
                onCheckedChange = vm::onShowPointsChange,
                color = GraphColors.points,
            )
            CheckRow(
                label = "Отображать график функции",
                checked = vm.showFunction,
                onCheckedChange = vm::onShowFunctionChange,
                color = GraphColors.function,
            )
            CheckRow(
                label = "Отображать производную",
                checked = vm.showDerivative,
                onCheckedChange = vm::onShowDerivativeChange,
                color = GraphColors.derivative,
            )
        }
    }
}


@Composable
private fun BoundInput(
    label: String,
    value: Double,
    onStep: (Double) -> Unit,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf(formatValue(value)) }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // синхронизация текста поля с состоянием модели представления
    LaunchedEffect(value, isFocused) {
        if (!isFocused) text = formatValue(value)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, fontSize = 13.sp, modifier = Modifier.width(42.dp))
        BasicTextField(
            value = text,
            onValueChange = { input ->
                if (isNumberInput(input)) {
                    text = input
                    input.toDoubleOrNull()?.let(onValueChange)
                }
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 13.sp,
                color = Color.Black,
                textAlign = TextAlign.End,
            ),
            cursorBrush = SolidColor(Color.Black),
            modifier = Modifier
                .width(66.dp)
                .background(Color.White)
                .border(1.dp, Color.Gray)
                .padding(horizontal = 4.dp, vertical = 3.dp)
                .onFocusChanged { isFocused = it.isFocused },
        )
        Column {
            StepButton(up = true) {
                focusManager.clearFocus()
                onStep(MainViewModel.STEP)
            }
            StepButton(up = false) {
                focusManager.clearFocus()
                onStep(-MainViewModel.STEP)
            }
        }
    }
}


@Composable
private fun StepButton(up: Boolean, onClick: () -> Unit) {
    Canvas(
        modifier = Modifier
            .size(width = 18.dp, height = 12.dp)
            .background(Color(0xFFE8E8E8))
            .border(0.5.dp, Color.Gray)
            .clickable(onClick = onClick),
    ) {
        val w = size.width
        val h = size.height
        val triangle = Path().apply {
            if (up) {
                moveTo(w / 2f, h * 0.2f)
                lineTo(w * 0.25f, h * 0.72f)
                lineTo(w * 0.75f, h * 0.72f)
            } else {
                moveTo(w * 0.25f, h * 0.28f)
                lineTo(w * 0.75f, h * 0.28f)
                lineTo(w / 2f, h * 0.8f)
            }
            close()
        }
        drawPath(triangle, Color(0xFF404040))
    }
}

@Composable
private fun CheckRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    color: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF303030),
                uncheckedColor = Color(0xFF606060),
                checkmarkColor = Color.White,
            ),
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(text = label, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(color)
                .border(1.dp, Color.DarkGray)
        )
    }
}


private fun formatValue(value: Double): String {
    val rounded = round(value * 10.0) / 10.0
    if (abs(rounded) < 1e-9) return "0.0"
    val integer = rounded.toInt()
    return if (rounded == integer.toDouble()) "$integer.0" else rounded.toString()
}

private fun isNumberInput(input: String): Boolean =
    input.isEmpty() || input == "-" || NUMBER_REGEX.matches(input)

private val NUMBER_REGEX = Regex("-?\\d{0,3}(\\.\\d{0,2})?")
