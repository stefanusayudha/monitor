package com.singularityindonesia.monitor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.singularityindonesia.monitor.chart.donut.PreviewRotatedDonutSliceExample
import com.singularityindonesia.monitor.chart.race.PreviewRacerExample
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PreviewRotatedDonutSliceExample()
            PreviewRacerExample()
        }
    }
}