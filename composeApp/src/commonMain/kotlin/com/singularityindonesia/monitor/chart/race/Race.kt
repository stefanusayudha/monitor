package com.singularityindonesia.monitor.chart.race

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * @param thickness ratio from 0f until 1f
 */
@Stable
class RaceConfig(
    val maxWeight: Number? = null,
    val thickness: Float = .7f,
    val animationDuration: Duration = 2.seconds,
)

private data class Racer<T : Any>(
    val item: ChartItem<T>,
    val sweepAngleDegrees: Float,
    val startAngleDegrees: Float
)

@Composable
fun <T : Any> Race(
    modifier: Modifier = Modifier,
    config: RaceConfig = RaceConfig(),
    items: List<ChartItem<T>>
) {
    val scope = rememberCoroutineScope()
    val startAnimation = rememberSaveable { mutableStateOf(false) }
    val animation = animateFloatAsState(
        targetValue = if (startAnimation.value) 1f else 0f,
        animationSpec = tween(
            if (startAnimation.value) config.animationDuration.inWholeMilliseconds.toInt() else 0,
            0,
        ),
    )

    val trackWeight = config.maxWeight ?: items.map { it.value }.maxByOrNull { it.toDouble() } ?: 1.0

    val racers = items.fold(emptyList<Racer<T>>()) { accumulator, item ->
        val wipeAngle = item.value.toFloat() / trackWeight.toFloat() * FULL_CIRCLE_DEGREE_ANGLE
        val racer = Racer(
            item = item,
            sweepAngleDegrees = wipeAngle,
            startAngleDegrees = Y_AXIS_START_ANGLE,
        )

        accumulator + racer
    }

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable {
                scope.launch {
                    startAnimation.value = false
                    delay(200)
                    startAnimation.value = true
                }
            }
    ) {
        val offsetCentralizer = Offset(
            x = if (size.width > size.height) center.x - size.width else 0f,
            y = if (size.height > size.width) center.y - size.height else 0f,
        )
        val diameter = minOf(size.width, size.height)
        val radius = diameter / 2
        val thickness = radius * config.thickness

        drawIntoCanvas {
            racers.mapIndexed { index, racer ->
                val sweepAngleDegrees = racer.sweepAngleDegrees * animation.value

                val outerDiameter = diameter - index * thickness
                val innerDiameter = diameter - (index + 1) * thickness
                val offset = offsetCentralizer + Offset(x = thickness / 2 * index, y = thickness / 2 * index)

                val track = donut(
                    offset = offset,
                    outerDiameter = outerDiameter,
                    innerDiameter = innerDiameter,
                    startAngleDegrees = Y_AXIS_START_ANGLE + 1f,
                    sweepAngleDegrees = FULL_CIRCLE_DEGREE_ANGLE,
                )

                drawPath(
                    path = track,
                    color = racer.item.color.copy(alpha = .02f),
                )

                val progress = donut(
                    offset = offset,
                    outerDiameter = outerDiameter,
                    innerDiameter = innerDiameter,
                    startAngleDegrees = Y_AXIS_START_ANGLE,
                    sweepAngleDegrees = sweepAngleDegrees,
                )

                drawPath(
                    path = progress,
                    color = racer.item.color,
                )

                drawCircle(
                    color = racer.item.color,
                    radius = thickness / 4f,
                    center = center - Offset(x = 0f, y= innerDiameter/2 + thickness/4)
                )

                drawCircle(
                    color = racer.item.color,
                    radius = thickness / 4f,
                    center = center + Offset(
                        x = (innerDiameter/2 + thickness/4) * sin(sweepAngleDegrees / 180 * 3.14f),
                        y = - (innerDiameter/2 + thickness/4) * cos(sweepAngleDegrees / 180 * 3.14f)
                    )
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
fun PreviewRacerExample() {
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
        Race(
            modifier = Modifier.size(200.dp),
            items = items,
            config = RaceConfig(thickness = .3f, maxWeight = 50f),
        )

        Race(
            modifier = Modifier.size(200.dp),
            items = items,
            config = RaceConfig(thickness = .2f, maxWeight = 50f),
        )
    }
}
