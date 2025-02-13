package com.singularityindonesia.monitor.chart

import androidx.compose.ui.graphics.Color

class ChartItem<T>(
    val data: T,
    val value: Number,
    val label: String,
    val desc: String = "",
    val color: Color = Color.Red,
)
