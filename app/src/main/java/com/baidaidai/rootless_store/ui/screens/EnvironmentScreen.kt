package com.baidaidai.rootless_store.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.ui.components.pluginsScreen.PluginActionPanel
import com.baidaidai.rootless_store.ui.components.pluginsScreen.InstalledManifestCard
import com.baidaidai.rootless_store.ui.model.RootlessStorePluginScreenViewModel

@Composable
fun InstalledEnvironmentList(
    isBadgeVisible: Boolean,
    environments: List<EnvironmentManifestRoom>,
    pluginScreenViewModel: RootlessStorePluginScreenViewModel,
){

    val density = LocalDensity.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            vertical = 15.dp,
            horizontal = 15.dp
        )
    ) {
        items(
            items = environments,
            key = { environmentManifestRoom -> environmentManifestRoom.environmentId }
        ){ environmentManifestRoom ->

            var isActionPanelVisible by remember { mutableStateOf(false) }
            var cardSize by remember { mutableStateOf(IntSize.Zero) }

            if (isActionPanelVisible){

                PluginActionPanel(
                    onShareButtonClick = {

                        val shareLink = pluginScreenViewModel.resolveEnvironmentShareUri(environmentManifestRoom)

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/zip"
                            putExtra(Intent.EXTRA_STREAM, shareLink)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        context.startActivity(Intent.createChooser(shareIntent, "Share plugin"))

                    },
                    onDeleteButtonClick = { pluginScreenViewModel.uninstallEnvironment(environmentManifestRoom) },
                    onBackButtonClick = { isActionPanelVisible = !isActionPanelVisible },
                    modifier = Modifier
                        .size(
                            width = with(density) { cardSize.width.toDp() },
                            height = with(density) { cardSize.height.toDp() }
                        )
                )

            }else{

                InstalledManifestCard(
                    environmentManifest = environmentManifestRoom,
                    onSwitchClick = {
                        pluginScreenViewModel.setEnvironmentEnabled(
                            environmentId = environmentManifestRoom.environmentId,
                            isEnabled = !environmentManifestRoom.isEnabled
                        )
                    },
                    onCardLongClick = { isActionPanelVisible = !isActionPanelVisible },
                    onCardSizeChanged = { cardSize = it },
                )

            }
        }
    }
}
