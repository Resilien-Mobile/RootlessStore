package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.components.pluginsScreen.PluginInfoContainerLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.ui.model.RootLessStorePluginScreenViewModel

@Composable
fun EnvironmentScreen(
    badgeShowState: Boolean,
    renderingList: List<EnvironmentManifestRoom>,
    pluginScreenViewModel: RootLessStorePluginScreenViewModel,
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            vertical = 15.dp,
            horizontal = 15.dp
        )
    ) {
        items(renderingList){
            PluginInfoContainerLocal(
                environmentManifest = it,
                onSwitchClick = {
                    pluginScreenViewModel.setEnvironmentEnabled(
                        environmentID = it.environmentID,
                        environmentEnabledStatus = !it.enabled
                    )
                },
                onBadgeClick = {
                    pluginScreenViewModel.uninstallEnvironment(it)
                },
                onCardClick = {},
                badgeShowState = badgeShowState
            )
        }
    }
}