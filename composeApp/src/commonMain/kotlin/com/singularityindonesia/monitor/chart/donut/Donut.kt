package com.singularityindonesia.monitor.chart.donut

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import com.singularityindonesia.monitor.chart.ChartItem
import com.singularityindonesia.monitor.chart.FULL_CIRCLE_DEGREE_ANGLE
import com.singularityindonesia.monitor.chart.Y_AXIS_START_ANGLE
import com.singularityindonesia.monitor.chart.donut
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * @param thickness ratio from 0f until 1f
 */
@Stable
class DonutConfig(
    val thickness: Float = .5f,
    val animationDuration: Duration = 2.seconds,
)

private data class Donut<T : Any>(
    val item: ChartItem<T>,
    val sweepAngleDegrees: Float,
    val startAngleDegrees: Float
)

@Composable
fun <T : Any> Donut(
    modifier: Modifier = Modifier,
    config: DonutConfig = DonutConfig(),
    items: List<ChartItem<T>>
) {
    val startAnimation = rememberSaveable { mutableStateOf(false) }
    val animation = animateFloatAsState(
        targetValue = if (startAnimation.value) 1f else 0f,
        animationSpec = tween(
            if (startAnimation.value) config.animationDuration.inWholeMilliseconds.toInt() else 0,
            0,
        ),
    )

    val totalWeight = items.map { it.value.toFloat() }.sum()

    val donuts = items.fold(emptyList<Donut<T>>()) { accumulator, item ->
        val startAngle = accumulator.map { it.sweepAngleDegrees }.sum()
        val wipeAngle = item.value.toFloat() / totalWeight * FULL_CIRCLE_DEGREE_ANGLE
        val donutSlice = Donut(
            item = item,
            sweepAngleDegrees = wipeAngle,
            startAngleDegrees = startAngle,
        )

        accumulator + donutSlice
    }

    Canvas(
        modifier = Modifier
            .then(modifier)
    ) {
        val outerDiameter = size.width
        val innerDiameter = outerDiameter * (1f - config.thickness)

        val outerRadius = outerDiameter / 2f
        val offsetCentralizer = Offset(
            x = if (size.width > size.height) center.x - outerRadius else 0f,
            y = if (size.height > size.width) center.y - outerRadius else 0f,
        )

        drawIntoCanvas {
            donuts.map { donut ->
                val sliceStartAngleDegrees = (donut.startAngleDegrees + FULL_CIRCLE_DEGREE_ANGLE) * animation.value
                val sweepAngleDegrees = donut.sweepAngleDegrees * animation.value

                // Rotate the canvas
                val path = donut(
                    offset = offsetCentralizer,
                    outerDiameter = outerDiameter,
                    innerDiameter = innerDiameter,
                    startAngleDegrees = Y_AXIS_START_ANGLE + sliceStartAngleDegrees,
                    sweepAngleDegrees = sweepAngleDegrees,
                )
                drawPath(
                    path = path,
                    color = donut.item.color,
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        startAnimation.value = true
    }
}

@Suppress("detekt:all")
@Preview
@Composable
fun PreviewRotatedDonutSliceExample() {
    val data = remember { listOf(10f, 20f, 30f, 40f) }
    val items = remember(data) {
        data.fold(emptyList<ChartItem<Float>>()) { accumulator, value ->
            val slice = ChartItem(
                data = value,
                value = value,
                label = "$value",
                color = when (value) {
                    10f -> Color.Red
                    20f -> Color.Blue
                    30f -> Color.Green
                    else -> Color.Magenta
                },
            )
            accumulator + slice
        }
    }

    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Donut(
            modifier = Modifier.size(200.dp),
            items = items,
            config = DonutConfig(thickness = 1f),
        )

        Donut(
            modifier = Modifier.size(200.dp),
            items = items,
            config = DonutConfig(thickness = .4f),
        )
    }
}
