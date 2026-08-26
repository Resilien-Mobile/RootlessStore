package com.baidaidai.rootless_store.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.baidaidai.rootless_store.WebViewActivity
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.ui.adaptive.RootlessStoreWindowSize
import com.baidaidai.rootless_store.ui.components.pluginScreen.PluginActionPanel
import com.baidaidai.rootless_store.ui.components.pluginScreen.InstalledManifestCard
import com.baidaidai.rootless_store.ui.model.RootlessStoreExecuteScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun PluginScreen(
    contentPadding: PaddingValues,
    rootlessStoreWidthWindowSize: RootlessStoreWindowSize,
    pluginScreenViewModel: RootlessStorePluginScreenViewModel,
    viewModelStoreOwner: ViewModelStoreOwner,
    onNavigateToExecuteScreen: (pluginId: String, shouldExecuteImmediately: Boolean) -> Unit,
    onAbortPluginProcess: suspend (pluginId: String) -> Unit,
    onExecuteOneTimePlugin: (pluginId: String) -> Unit
){
    val plugins by pluginScreenViewModel.plugins.collectAsState()
    val environments by pluginScreenViewModel.environments.collectAsState()

    // 切换 Environment / Plugin
    var selectedTabIndex by rememberSaveable{ mutableIntStateOf(0) }
    var pluginId by remember { mutableStateOf("") }
    var shouldExecuteImmediately by remember { mutableStateOf(false) }
    val executeScreenViewModel = hiltViewModel<RootlessStoreExecuteScreenViewModel>(key = pluginId, viewModelStoreOwner = viewModelStoreOwner)


    Row(modifier = Modifier.fillMaxSize().padding(contentPadding)) {

        Column(modifier = Modifier.weight(1f).fillMaxSize()) {
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
                        plugins = plugins,
                        pluginScreenViewModel = pluginScreenViewModel,
                        onNavigateToExecuteScreen = { _pluginId, _shouldExecuteImmediately ->
                            pluginId = _pluginId ; shouldExecuteImmediately = _shouldExecuteImmediately

                            if (rootlessStoreWidthWindowSize == RootlessStoreWindowSize.Compact){
                                onNavigateToExecuteScreen(_pluginId,_shouldExecuteImmediately)
                            }
                        },
                        onAbortPluginProcess = onAbortPluginProcess,
                        onExecuteOneTimePlugin = onExecuteOneTimePlugin
                    )
                }
                1 -> {
                    InstalledEnvironmentList(
                        environments = environments,
                        pluginScreenViewModel = pluginScreenViewModel
                    )
                }
            }
        }

        if (rootlessStoreWidthWindowSize != RootlessStoreWindowSize.Compact){

            if (pluginId.isNotEmpty()){
                LaunchedEffect(pluginId, shouldExecuteImmediately) {
                    if (shouldExecuteImmediately) {
                        executeScreenViewModel.executePlugin(pluginId)
                    }
                }

                ExecuteScreen(
                    contentPaddingValues = PaddingValues(0.dp),
                    executeScreenViewModel = executeScreenViewModel,
                    modifier = Modifier.weight(1f)
                )
            }else{
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {

                    Icon(
                        painter = painterResource(R.drawable.terminal_24px),
                        modifier = Modifier.size(64.dp),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                    )

                }
            }

        }

    }

}

/**
 * shouldExecuteImmediately只是用来控制一种情况
 * 就是是否 Switch 杀插件，因为OneTime已经有对应的CallBack了
 */
@Composable
fun InstalledPluginList(
    plugins: List<PluginManifestRoom>,
    pluginScreenViewModel: RootlessStorePluginScreenViewModel,
    onNavigateToExecuteScreen: (pluginId: String, shouldExecuteImmediately: Boolean) -> Unit,
    onAbortPluginProcess: suspend (pluginId: String) -> Unit,
    onExecuteOneTimePlugin: (pluginId: String) -> Unit
){
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val context = LocalContext.current

    if (plugins.isEmpty()){

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
                text = "No Plugin Installed",
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
                items = plugins,
                key = { pluginManifestRoom -> pluginManifestRoom.pluginId }
            ){ pluginManifestRoom ->

                var isActionPanelVisible by remember { mutableStateOf(false) }
                var cardSize by remember { mutableStateOf(IntSize.Zero) }

                if (isActionPanelVisible){

                    PluginActionPanel(
                        pluginManifestRoom = pluginManifestRoom,
                        onShareClick = {
                            val shareUri = pluginScreenViewModel.resolvePluginShareUri(pluginManifestRoom)

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/zip"
                                putExtra(Intent.EXTRA_STREAM, shareUri)
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
                                onNavigateToExecuteScreen(pluginManifestRoom.pluginId,true)
                            }else{
                                coroutineScope.launch {
                                    onAbortPluginProcess(pluginManifestRoom.pluginId)
                                }
                            }
                        },
                        onExecuteClick = { onExecuteOneTimePlugin(pluginManifestRoom.pluginId) },
                        onClick = {
                            if (pluginManifestRoom.isEnabled){
                                onNavigateToExecuteScreen(pluginManifestRoom.pluginId,false)
                            }
                        },
                        onLongClick = { isActionPanelVisible = !isActionPanelVisible },
                        onSizeChanged = { cardSize = it },
                    )

                }
            }
        }

    }
}
