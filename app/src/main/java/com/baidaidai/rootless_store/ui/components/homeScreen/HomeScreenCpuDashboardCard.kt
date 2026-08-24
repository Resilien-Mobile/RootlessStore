package com.baidaidai.rootless_store.ui.components.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.domain.status.model.CpuDashboardConfig



@Composable
fun HomeScreenCpuDashboardCard(
    modifier: Modifier = Modifier,
    cpuDashboardConfig: CpuDashboardConfig
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(20.dp)
    ) {
        // Primary data
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            Text("%04.1f".format((cpuDashboardConfig.aggregateMetrics.totalPercent*100)), style = MaterialTheme.typography.headlineLarge)
            HomeScreenMetricCell(
                title = "SYS",
                value = cpuDashboardConfig.aggregateMetrics.systemDelta.toString()
            )
            HomeScreenMetricCell(
                title = "USER",
                value = cpuDashboardConfig.aggregateMetrics.userDelta.toString()
            )
            HomeScreenMetricCell(
                title = "IO WAIT",
                value = cpuDashboardConfig.aggregateMetrics.ioWaitDelta.toString()
            )
            HomeScreenMetricCell(
                title = "STEAL",
                value = cpuDashboardConfig.aggregateMetrics.stealDelta.toString()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dot Line
        BoxWithConstraints(){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                repeat(cpuDashboardConfig.coreCount){ index->
                    HomeScreenCpuUsageBar(
                        percent = cpuDashboardConfig.coreMetrics[index].totalPercent,
                        maxWidth = this@BoxWithConstraints.maxWidth
                    )
                    if(index != cpuDashboardConfig.coreCount-1){
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Data Two
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .fillMaxWidth()
        ) {
            HomeScreenMetricCell(
                title = "CORES",
                value = cpuDashboardConfig.coreCount.toString()
            )
            HomeScreenMetricCell(
                title = "IDLE",
                value = cpuDashboardConfig.aggregateMetrics.idleDelta.toString()
            )
            HomeScreenMetricCell(
                title = "UPTIME",
                value = "${cpuDashboardConfig.uptime.inWholeSeconds} S"
            )
            StatusCircularProgress(label = "Pressure", percentage = cpuDashboardConfig.aggregateMetrics.totalPercent)
        }

    }
}

@PreviewLightDark
@Composable
private fun _preview_() {
    Column(modifier = Modifier.fillMaxWidth()) {
        HomeScreenCpuDashboardCard(
            cpuDashboardConfig = CpuDashboardConfig._testOnly_,
            modifier = Modifier
                .width(200.dp)
                .height(280.dp)
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}
