package com.baidaidai.rootless_store.ui.components.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.DeviceStatusSummary
import com.baidaidai.rootless_store.domain.status.model.StorageStatus

@Composable
fun HomeStatusOverview(
    modifier: Modifier = Modifier,
    deviceStatus: DeviceStatusSummary,
    onChipClick: ()-> Unit = {},
    onChipLongClick: ()-> Unit = {}
){
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ){
            Row(
                modifier = Modifier
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(IntrinsicSize.Min)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_monitoring),
                            contentDescription = stringResource(R.string.home_screen_device_status_icon_content_description),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxHeight()
                        )
                        Spacer(
                            modifier = Modifier
                                .width(10.dp)
                        )
                        Text(
                            text = stringResource(R.string.home_screen_device_status_title),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight(Alignment.CenterVertically)
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .height(4.dp)
                    )
                    Row {
                        ExecutionContextChip(
                            deviceStatus = deviceStatus,
                            onLongClick = onChipLongClick,
                            onClick = onChipClick,
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                StatusCircularProgress(
                    label = stringResource(R.string.home_screen_device_status_memory_label),
                    currentValue = deviceStatus.memoryStatus.usedMemory,
                    maxValue = deviceStatus.memoryStatus.totalMemory
                )
                StatusCircularProgress(
                    label = stringResource(R.string.home_screen_device_status_storage_label),
                    currentValue = deviceStatus.storageStatus.usedStorage,
                    maxValue = deviceStatus.storageStatus.totalStorage
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun _preview_() {
    Column(modifier = Modifier.fillMaxWidth()) {
        HomeStatusOverview(
            modifier = Modifier.width(300.dp),
            deviceStatus = DeviceStatusSummary()
                .copy(
                    executionContext = ExecutionContext.LIMITED,
                    memoryStatus = MemoryStatus(totalMemory = 256.01, usedMemory = 128.64),
                    storageStatus = StorageStatus(totalStorage = 512.10, usedStorage = 128.64)
                )
        )
    }
}