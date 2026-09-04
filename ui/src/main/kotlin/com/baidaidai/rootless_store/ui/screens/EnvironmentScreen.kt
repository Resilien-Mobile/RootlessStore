package com.baidaidai.rootless_store.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.ui.R
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.ui.components.pluginScreen.PluginActionPanel
import com.baidaidai.rootless_store.ui.components.pluginScreen.InstalledManifestCard
import com.baidaidai.rootless_store.ui.model.RootlessStorePluginScreenViewModel

@Composable
fun InstalledEnvironmentList(
    environments: List<EnvironmentManifestRoom>,
    pluginScreenViewModel: RootlessStorePluginScreenViewModel,
){

    val density = LocalDensity.current
    val context = LocalContext.current

    if (environments.isEmpty()){

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Icon(
                painter = painterResource(R.drawable.material_symbols_folder_open),
                modifier = Modifier.size(64.dp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
            )
            Text(
                text = "No Environment Installed",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
            )
        }

    }else{

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
                        onShareClick = {

                            val shareUri = pluginScreenViewModel.resolveEnvironmentShareUri(environmentManifestRoom)

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/zip"
                                putExtra(Intent.EXTRA_STREAM, shareUri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }

                            context.startActivity(Intent.createChooser(shareIntent, "Share plugin"))

                        },
                        onUninstallClick = { pluginScreenViewModel.uninstallEnvironment(environmentManifestRoom) },
                        onDismissClick = { isActionPanelVisible = !isActionPanelVisible },
                        modifier = Modifier
                            .size(
                                width = with(density) { cardSize.width.toDp() },
                                height = with(density) { cardSize.height.toDp() }
                            )
                    )

                }else{

                    InstalledManifestCard(
                        environmentManifest = environmentManifestRoom,
                        onEnabledChange = { isEnabled ->
                            pluginScreenViewModel.setEnvironmentEnabled(
                                environmentId = environmentManifestRoom.environmentId,
                                isEnabled = isEnabled
                            )
                        },
                        onLongClick = { isActionPanelVisible = !isActionPanelVisible },
                        onSizeChanged = { cardSize = it },
                    )

                }
            }
        }

    }
}
