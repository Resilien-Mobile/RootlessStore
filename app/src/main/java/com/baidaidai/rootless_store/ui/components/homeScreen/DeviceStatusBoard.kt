package com.baidaidai.rootless_store.ui.components.homeScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.status.model.DeviceStatusSummary

@Composable
fun DeviceStatusBoard(
    modifier: Modifier = Modifier,
    deviceStatus: DeviceStatusSummary
){
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
    Card(
        colors = cardColors,
        modifier = modifier,
        elevation = CardDefaults.cardElevation()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.clip(MaterialTheme.shapes.medium)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 20.dp)
            ) {
                DeviceStatusRow(
                    stringResource(R.string.home_screen_device_status_version_label),
                    "${deviceStatus.androidAndApiStatus?.androidVersion} (${deviceStatus.androidAndApiStatus?.apiVersion})"
                )
                Spacer(
                    modifier = Modifier
                        .height(8.dp)
                )
                DeviceStatusRow(
                    stringResource(R.string.home_screen_device_status_kernel_label),
                    deviceStatus.kernelVersion ?: "null"
                )
                Spacer(
                    modifier = Modifier
                        .height(8.dp)
                )
                DeviceStatusRow(
                    stringResource(R.string.home_screen_device_status_selinux_label),
                    deviceStatus.seLinuxStatus.toString()
                )
                Spacer(
                    modifier = Modifier
                        .height(8.dp)
                )
                DeviceStatusRow(
                    stringResource(R.string.home_screen_device_status_plugins_label),
                    "${deviceStatus.pluginStatus.enabledCount}/${deviceStatus.pluginStatus.totalCount}"
                )
                Spacer(
                    modifier = Modifier
                        .height(8.dp)
                )
                DeviceStatusRow(
                    stringResource(R.string.home_screen_device_status_temp_label),
                    deviceStatus.temperatureStatus?.toString() ?: "null"
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun _preview_() {
    Column(modifier = Modifier.fillMaxWidth()) {
        DeviceStatusBoard(
            deviceStatus = DeviceStatusSummary(),
            modifier = Modifier.width(200.dp)
        )
    }
}
