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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.tooling.preview.Preview
import com.baidaidai.rootless_store.domain.status.model.NetDashboardConfig
import com.baidaidai.rootless_store.domain.status.model.PortInfo
import com.baidaidai.rootless_store.ui.adaptive.RootlessStoreWindowSize

@Composable
fun HomeScreenNetDashboard(
    modifier: Modifier = Modifier,
    netDashboardConfig: NetDashboardConfig,
    rootlessStoreHeightWindowSize: RootlessStoreWindowSize
){
    val downloadSpeedList = rememberSaveable { mutableStateListOf(0f,0f,0f,0f,0f,0f,0f,0f) }

    LaunchedEffect(netDashboardConfig.currentDownloadRate) {
        downloadSpeedList.removeAt(0)
        downloadSpeedList.add(netDashboardConfig.currentDownloadRate)
    }

    val dynamicallyAdjustedModifier = when(rootlessStoreHeightWindowSize){
        RootlessStoreWindowSize.Compact -> modifier.fillMaxHeight()
        else -> modifier.height(280.dp)
    }

    if (rootlessStoreHeightWindowSize != RootlessStoreWindowSize.Compact){
        Column(
            modifier = dynamicallyAdjustedModifier
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
                modifier = Modifier.fillMaxWidth()
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
    }else{
        Row(
            modifier = dynamicallyAdjustedModifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
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

            Spacer(modifier = Modifier.width(15.dp))

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
            ) {
                netDashboardConfig.port.forEachIndexed { index, portInfo ->
                    HomeScreenNetDashboardTie(portInfo = portInfo)
                    if (index != netDashboardConfig.port.size-1){
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(Modifier.height(1.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                    }
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
    Column(modifier = modifier) {
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
            Spacer(modifier = Modifier.width(10.dp))
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
            Spacer(modifier = Modifier.width(10.dp))
            HomeScreenCpuInfoCell(
                title = "↓/S",
                value = "${"%.1f".format(portInfo.currentDownloadRate)} M/s"
            )
            Spacer(modifier = Modifier.width(10.dp))
            Row {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) { Text("↑") ; Spacer(modifier = Modifier.width(10.dp)) ; Text("${"%.1f".format(portInfo.totalUploadPackage)} M")}
                    Row(verticalAlignment = Alignment.CenterVertically) { Text("↓") ; Spacer(modifier = Modifier.width(10.dp)) ; Text("${"%.1f".format(portInfo.totalDownloadPackage)} M")}
                }
                Spacer(modifier = Modifier.width(20.dp))
                CircularProgressIndicator(
                    progress = {
                        (portInfo.totalUploadPackage/(portInfo.totalDownloadPackage+portInfo.totalUploadPackage))
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f,true)
                )
            }
        }

    }
}

@Preview(
    widthDp = 500,
    heightDp = 220
)
@Composable
private fun _previewBoardCompact_() {
    Column(modifier = Modifier.width(500.dp)) {
        HomeScreenNetDashboard(
            netDashboardConfig = NetDashboardConfig._testOnly_,
            rootlessStoreHeightWindowSize = RootlessStoreWindowSize.Compact,
            modifier = Modifier
                .height(500.dp)
                .width(500.dp)
        )
    }

}
@Preview(
    widthDp = 500,
    heightDp = 500
)
@Composable
private fun _previewBoardExpanded_() {
    Column(modifier = Modifier.width(500.dp)) {
        HomeScreenNetDashboard(
            netDashboardConfig = NetDashboardConfig._testOnly_,
            rootlessStoreHeightWindowSize = RootlessStoreWindowSize.Expanded,
            modifier = Modifier
                .height(500.dp)
                .width(500.dp)
        )
    }

}

@PreviewLightDark
@Composable
private fun _previewTie_() {
    Column(modifier = Modifier.fillMaxWidth()) {
        HomeScreenNetDashboardTie(
            portInfo = NetDashboardConfig._testOnly_.port[0],
            modifier = Modifier
                .width(200.dp)
                .background(MaterialTheme.colorScheme.background)
        )
    }

}