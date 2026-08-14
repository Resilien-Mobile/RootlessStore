package com.baidaidai.rootless_store.ui.components.homeScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.baidaidai.rootless_store.domain.status.model.NetDashboardConfig
import com.baidaidai.rootless_store.domain.status.model.PortInfo

@Composable
fun HomeScreenNetDashboard(
    modifier: Modifier = Modifier,
    netDashboardConfig: NetDashboardConfig
){
    val downloadSpeedList = rememberSaveable { mutableStateListOf(0f,0f,0f,0f,0f,0f,0f,0f) }

    LaunchedEffect(netDashboardConfig.currentDownloadRate) {
        downloadSpeedList.removeAt(0)
        downloadSpeedList.add(netDashboardConfig.currentDownloadRate)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(4))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(4)
                )
        ){
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 5.dp, bottom = 10.dp)
            ) {
                val maxValue = downloadSpeedList.maxOrNull() ?: return@Canvas
                val minValue = downloadSpeedList.minOrNull() ?: return@Canvas
                val valueRange = (maxValue - minValue).takeIf { it > 0f } ?: 1f
                val stepX = size.width / downloadSpeedList.lastIndex
                val path = Path()

                downloadSpeedList.forEachIndexed { index, float ->
                    val x = stepX * index
                    val progress = (float - minValue) / valueRange
                    val y = size.height * (1f - progress)
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }

                }
                drawPath(
                    path = path,
                    color = Color(0xFF49679E),
                    style = Stroke(
                        width = 2f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier

        ) {
            netDashboardConfig.port.forEachIndexed { index, portInfo ->
                HomeScreenNetDashboardTie(portInfo = portInfo)
                if (index != netDashboardConfig.port.size-1){
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(Modifier
                        .fillMaxWidth()
                        .height(1.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeScreenNetDashboardTie(
    modifier: Modifier = Modifier,
    portInfo: PortInfo
){
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(portInfo.portIcon),
                    contentDescription = portInfo.portName
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(portInfo.portName)
            }
            Text(portInfo.portAddress)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
        ) {
            HomeScreenCpuInfoCell(
                title = "↑/S",
                value = "${"%.1f".format(portInfo.currentUploadRate)} M/s"
            )
            HomeScreenCpuInfoCell(
                title = "↓/S",
                value = "${"%.1f".format(portInfo.currentDownloadRate)} M/s"
            )
            Row {
                Column {
                    Row { Text("↑") ; Spacer(modifier = Modifier.width(20.dp)) ; Text("${"%.1f".format(portInfo.totalUploadPackage)}M")}
                    Row { Text("↓") ; Spacer(modifier = Modifier.width(20.dp)) ; Text("${"%.1f".format(portInfo.totalDownloadPackage)}M")}
                }
                Spacer(modifier = Modifier.width(20.dp))
                CircularProgressIndicator(
                    progress = {
                        (portInfo.totalUploadPackage/(portInfo.totalDownloadPackage+portInfo.totalUploadPackage))
                    }
                )
            }
        }

    }
}

@PreviewLightDark
@Composable
private fun _previewBoard_() {
    HomeScreenNetDashboard(
        netDashboardConfig = NetDashboardConfig._testOnly_,
        modifier = Modifier
            .height(500.dp)
    )
}

@PreviewLightDark
@Composable
private fun _previewTie_() {
    HomeScreenNetDashboardTie(
        portInfo = NetDashboardConfig._testOnly_.port[0],
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    )
}