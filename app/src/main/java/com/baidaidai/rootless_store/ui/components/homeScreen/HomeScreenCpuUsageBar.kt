package com.baidaidai.rootless_store.ui.components.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreenCpuUsageBar(
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    percent: Float = 0.3f,
    isInverted: Boolean = false
){
    val primaryDotColor = if (isInverted) Color.Green else Color.Red
    val secondaryDotColor = if (isInverted) Color.Red else Color.Green
    val segmentCount = (maxWidth / 10.dp).toInt()
    val activeSegmentCount = (percent * segmentCount).toInt()

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        for(index in 0..< segmentCount){
            if(index < activeSegmentCount){
                HomeScreenCpuUsageBarSegment(color = primaryDotColor)
            }else{
                HomeScreenCpuUsageBarSegment(color = secondaryDotColor)
            }
            if (index != 50){
                Spacer(modifier = Modifier.width(5.dp))
            }
        }

    }

}

@Composable
private fun HomeScreenCpuUsageBarSegment(
    modifier: Modifier = Modifier,
    color: Color = Color.Red
){
    Box(
        modifier = modifier
            .height(10.dp)
            .width(5.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
    ) { }

}

@PreviewLightDark
@Composable
private fun _previewCpuUsageBarSegment_() {
    HomeScreenCpuUsageBarSegment()
}

@PreviewLightDark
@Composable
private fun _previewCpuUsageBar_() {
    BoxWithConstraints(
        modifier = Modifier.width(400.dp)
    ) {
        HomeScreenCpuUsageBar(maxWidth = maxWidth)
    }
}
