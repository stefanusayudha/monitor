package com.singularityindonesia.monitor.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path

const val FULL_CIRCLE_DEGREE_ANGLE = 360f
const val Y_AXIS_START_ANGLE = -90f

fun donut(
    offset: Offset,
    outerDiameter: Float,
    innerDiameter: Float,
    startAngleDegrees: Float,
    sweepAngleDegrees: Float,
): Path {
    val outerArcSize = Size(outerDiameter, outerDiameter)
    val innerArcSize = Size(innerDiameter, innerDiameter)
    val outerRadius = outerDiameter / 2f
    val innerRadius = innerDiameter / 2f
    val radiusDiff = outerRadius - innerRadius

    return Path().apply {
        // draw outside bow
        arcTo(
            rect = Rect(
                offset = offset,
                size = outerArcSize,
            ),
            startAngleDegrees = startAngleDegrees,
            sweepAngleDegrees = sweepAngleDegrees,
            forceMoveTo = false,
        )

        // draw inner bow
        arcTo(
            rect = Rect(
                offset = offset + Offset(radiusDiff, radiusDiff),
                size = innerArcSize,
            ),
            startAngleDegrees = startAngleDegrees + sweepAngleDegrees,
            sweepAngleDegrees = -(sweepAngleDegrees),
            forceMoveTo = false,
        )

        close()
    }
}
