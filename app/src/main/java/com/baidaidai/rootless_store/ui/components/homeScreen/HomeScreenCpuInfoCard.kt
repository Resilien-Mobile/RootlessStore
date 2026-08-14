package com.baidaidai.rootless_store.ui.components.homeScreen

import androidx.compose.foundation.Image
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
fun HomeScreenCpuInfoCard(
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
        // Data One
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            Text("%04.1f".format((cpuDashboardConfig.totalCoreInfo.totalPercent*100)), style = MaterialTheme.typography.headlineLarge)
            HomeScreenCpuInfoCell(
                title = "SYS",
                value = cpuDashboardConfig.totalCoreInfo.systemDelta.toString()
            )
            HomeScreenCpuInfoCell(
                title = "USER",
                value = cpuDashboardConfig.totalCoreInfo.userDelta.toString()
            )
            HomeScreenCpuInfoCell(
                title = "IO WAIT",
                value = cpuDashboardConfig.totalCoreInfo.ioWaitDelta.toString()
            )
            HomeScreenCpuInfoCell(
                title = "STEAL",
                value = cpuDashboardConfig.totalCoreInfo.stealDelta.toString()
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
                    HomeScreenCpuInfoTie(
                        percent = cpuDashboardConfig.core[index].totalPercent,
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
            HomeScreenCpuInfoCell(
                title = "CORES",
                value = cpuDashboardConfig.coreCount.toString()
            )
            HomeScreenCpuInfoCell(
                title = "IDLE",
                value = cpuDashboardConfig.totalCoreInfo.idleDelta.toString()
            )
            HomeScreenCpuInfoCell(
                title = "UPTIME",
                value = "${cpuDashboardConfig.uptime.inWholeSeconds} S"
            )
            HosterStatusCircularProgress(label = "Pressure", percentage = cpuDashboardConfig.totalCoreInfo.totalPercent)
        }

    }
}

@PreviewLightDark
@Composable
private fun _preview_() {
    HomeScreenCpuInfoCard(
        cpuDashboardConfig = CpuDashboardConfig._testOnly_,
        modifier = Modifier
            .height(280.dp)
            .background(MaterialTheme.colorScheme.surface)
    )
}
