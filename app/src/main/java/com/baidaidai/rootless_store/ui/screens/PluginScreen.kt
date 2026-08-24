package com.baidaidai.rootless_store.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.ui.model.RootlessStorePluginScreenViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import com.baidaidai.rootless_store.WebViewActivity
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.ui.components.pluginsScreen.PluginActionPanel
import com.baidaidai.rootless_store.ui.components.pluginsScreen.InstalledManifestCard
import kotlinx.coroutines.launch

@Composable
fun PluginScreen(
    contentPadding: PaddingValues,
    pluginScreenViewModel: RootlessStorePluginScreenViewModel,
    navigateToExecuteScreen: (pluginId: String,shouldExecutePlugin: Boolean)-> Unit,
    onAbortPlugin:suspend (pluginId: String) -> Unit,
    onActivateOneTimePlugin: (pluginId: String)-> Unit
){
    val plugins by pluginScreenViewModel.plugins.collectAsState()
    val environments by pluginScreenViewModel.environments.collectAsState()
    val isBadgeVisible by pluginScreenViewModel.isBadgeVisible.collectAsState()

    var selectedTabIndex by rememberSaveable{ mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize(),
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f)
            ) {
                Text(stringResource(R.string.plugin_screen_secondary_tab_row_plugins_label))
            }

            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f)
            ) {
                Text(stringResource(R.string.plugin_screen_secondary_tab_row_environment_label))
            }
        }
        when(selectedTabIndex){
            0 -> {
                InstalledPluginList(
                    isBadgeVisible = isBadgeVisible,
                    plugins = plugins,
                    pluginScreenViewModel = pluginScreenViewModel,
                    navigateToExecuteScreen = navigateToExecuteScreen,
                    onAbortPlugin = onAbortPlugin,
                    onActivateOneTimePlugin = onActivateOneTimePlugin
                )
            }
            1 -> {
                InstalledEnvironmentList(
                    isBadgeVisible = isBadgeVisible,
                    environments = environments,
                    pluginScreenViewModel = pluginScreenViewModel
                )
            }
        }
    }
}

@Composable
fun InstalledPluginList(
    isBadgeVisible: Boolean,
    plugins: List<PluginManifestRoom>,
    pluginScreenViewModel: RootlessStorePluginScreenViewModel,
    navigateToExecuteScreen: (pluginId: String,shouldExecutePlugin: Boolean)-> Unit,
    onAbortPlugin: suspend (pluginId: String) -> Unit,
    onActivateOneTimePlugin: (pluginId: String)-> Unit
){
    val coroutineScope = rememberCoroutineScope()
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
            items = plugins,
            key = { pluginManifestRoom -> pluginManifestRoom.pluginId }
        ){ pluginManifestRoom ->

            var isActionPanelVisible by remember { mutableStateOf(false) }
            var cardSize by remember { mutableStateOf(IntSize.Zero) }

            if (isActionPanelVisible){

                PluginActionPanel(
                    pluginManifestRoom = pluginManifestRoom,
                    onShareClick = {
                        val shareLink = pluginScreenViewModel.resolvePluginShareUri(pluginManifestRoom)

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/zip"
                            putExtra(Intent.EXTRA_STREAM, shareLink)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        context.startActivity(Intent.createChooser(shareIntent, "Share plugin"))
                    },
                    onOpenWebUiClick = {

                        val webUiUri = pluginScreenViewModel.resolvePluginWebUiUri(pluginManifestRoom)

                        val webUiIntent = Intent(context, WebViewActivity::class.java).apply {
                            putExtra("webUri",webUiUri)
                        }
                        context.startActivity(webUiIntent)
                    },
                    onUninstallClick = { pluginScreenViewModel.uninstallPlugin(pluginManifestRoom) },
                    onDismissClick = { isActionPanelVisible = !isActionPanelVisible },
                    modifier = Modifier
                        .size(
                            width = with(density) { cardSize.width.toDp() },
                            height = with(density) { cardSize.height.toDp() }
                        )
                )

            }else{

                InstalledManifestCard(
                    pluginManifestRoom = pluginManifestRoom,
                    onEnabledChange = { isEnabled ->
                        pluginScreenViewModel.setPluginEnabled(
                            pluginId = pluginManifestRoom.pluginId,
                            isEnabled = isEnabled
                        )

                        if (isEnabled){
                            navigateToExecuteScreen(pluginManifestRoom.pluginId,true)
                        }else{
                            coroutineScope.launch {
                                onAbortPlugin(pluginManifestRoom.pluginId)
                            }
                        }
                    },
                    onExecuteClick = { onActivateOneTimePlugin(pluginManifestRoom.pluginId) },
                    onClick = {
                        if (pluginManifestRoom.isEnabled){
                            navigateToExecuteScreen(pluginManifestRoom.pluginId,false)
                        }
                    },
                    onLongClick = { isActionPanelVisible = !isActionPanelVisible },
                    onSizeChanged = { cardSize = it },
                )

            }
        }
    }
}
