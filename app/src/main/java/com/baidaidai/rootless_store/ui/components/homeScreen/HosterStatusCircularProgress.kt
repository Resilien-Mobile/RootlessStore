package com.baidaidai.rootless_store.ui.components.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@Composable
fun HosterStatusCircularProgress(label: String, currentValue: Double, maxValue: Double){
    val currentValueProgress = (currentValue / maxValue).toFloat()
    val currentValuePercentage = (currentValueProgress * 100).toInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = {
                    currentValueProgress
                },
                modifier = Modifier
                    .size(45.dp)
            )
            Text(
                text = "${currentValuePercentage.toString()}%",
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
@PreviewLightDark
private fun _HosterStatusCircularProgressRowPreview_(){
    Box(
        modifier = Modifier
            .background(color = Color.White)
    ) {
        HosterStatusCircularProgress(
            label = "RAM",
            currentValue = 139.32,
            maxValue = 512.00
        )
    }
}