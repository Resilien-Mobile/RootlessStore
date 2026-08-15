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
import com.baidaidai.rootless_store.domain.status.model.RootlessStoreHosterStatus

@Composable
fun RootlessStoreHosterStatusBoard(
    modifier: Modifier = Modifier,
    hosterStatus: RootlessStoreHosterStatus
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
                HosterStatusRow(
                    stringResource(R.string.home_screen_hoster_status_board_version_label),
                    "${hosterStatus.osAndAPIVersion?.androidVersion} (${hosterStatus.osAndAPIVersion?.apiVersion})"
                )
                Spacer(
                    modifier = Modifier
                        .height(8.dp)
                )
                HosterStatusRow(
                    stringResource(R.string.home_screen_hoster_status_board_kernel_label),
                    hosterStatus.kernelVersion ?: "null"
                )
                Spacer(
                    modifier = Modifier
                        .height(8.dp)
                )
                HosterStatusRow(
                    stringResource(R.string.home_screen_hoster_status_board_selinux_label),
                    hosterStatus.selinuxStatus.toString()
                )
                Spacer(
                    modifier = Modifier
                        .height(8.dp)
                )
                HosterStatusRow(
                    stringResource(R.string.home_screen_hoster_status_board_plugins_label),
                    "${hosterStatus.pluginStatus.enabledCount}/${hosterStatus.pluginStatus.totalCount}"
                )
                Spacer(
                    modifier = Modifier
                        .height(8.dp)
                )
                HosterStatusRow(
                    stringResource(R.string.home_screen_hoster_status_board_temp_label),
                    hosterStatus.tempStatus?.toString() ?: "null"
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun _preview_() {
    Column(modifier = Modifier.fillMaxWidth()) {
        RootlessStoreHosterStatusBoard(
            hosterStatus = RootlessStoreHosterStatus(),
            modifier = Modifier.width(200.dp)
        )
    }
}
