package com.baidaidai.rootless_store.ui.components.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@Composable
fun HosterStatusProgressRow(label: String, currentValue: Double,maxValue: Double){
    val currentValueProgress = (currentValue / maxValue).toFloat()
    var currentValuePercentage = (currentValueProgress * 100).toInt()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .weight(0.35f)
        )
        Column(
            modifier = Modifier
                .weight(0.5f),
        ) {
            LinearProgressIndicator(
                progress = {
                    currentValueProgress.toFloat()
                },
                drawStopIndicator = {}
            )
        }
        Text(
            text = "$currentValuePercentage %",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(0.15f),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
@PreviewLightDark
private fun _HosterStatusProgressRowPreview_(){
    Box(
        modifier = Modifier
            .background(color = Color.White)
    ) {
        HosterStatusProgressRow(
            label = "RAM",
            currentValue = 139.32,
            maxValue = 512.00
        )
    }
}
