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
import com.baidaidai.rootless_store.ui.model.RootLessStorePluginScreenViewModel
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
import com.baidaidai.rootless_store.ui.components.pluginsScreen.PluginActionContainer
import com.baidaidai.rootless_store.ui.components.pluginsScreen.PluginInfoContainerLocal
import kotlinx.coroutines.launch

@Composable
fun RootlessStorePluginScreenContainer(
    contentPadding: PaddingValues,
    pluginScreenViewModel: RootLessStorePluginScreenViewModel,
    navigateToExecuteScreen: (pluginID: String,isExecutePlugin: Boolean)-> Unit,
    onAbortOnePlugin:suspend (pluginID: String) -> Unit,
    onActiveOneTimePlugin: (pluginID: String)-> Unit
){
    val pluginInfoList by pluginScreenViewModel.pluginInfoList.collectAsState()
    val environmentInfoList by pluginScreenViewModel.environmentInfoList.collectAsState()
    val badgeShowState by pluginScreenViewModel.badgeShowState.collectAsState()

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
                PluginScreen(
                    badgeShowState = badgeShowState,
                    renderingList = pluginInfoList,
                    pluginScreenViewModel = pluginScreenViewModel,
                    navigateToExecuteScreen = navigateToExecuteScreen,
                    onAbortOnePlugin = onAbortOnePlugin,
                    onButtonClick = onActiveOneTimePlugin
                )
            }
            1 -> {
                EnvironmentScreen(
                    badgeShowState = badgeShowState,
                    renderingList = environmentInfoList,
                    pluginScreenViewModel = pluginScreenViewModel
                )
            }
        }
    }
}

@Composable
fun PluginScreen(
    badgeShowState: Boolean,
    renderingList: List<PluginManifestRoom>,
    pluginScreenViewModel: RootLessStorePluginScreenViewModel,
    navigateToExecuteScreen: (pluginID: String,isExecutePlugin: Boolean)-> Unit,
    onAbortOnePlugin: suspend (pluginID: String) -> Unit,
    onButtonClick: (pluginID: String)-> Unit
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
            items = renderingList,
            key = { pluginManifestRoom -> pluginManifestRoom.pluginID }
        ){ pluginManifestRoom ->

            var actionCanSee by remember { mutableStateOf(false) }
            var cardSize by remember { mutableStateOf(IntSize.Zero) }

            if (actionCanSee){

                PluginActionContainer(
                    pluginManifestRoom = pluginManifestRoom,
                    onShareButtonClick = {
                        val shareLink = pluginScreenViewModel.getPluginShareLink(pluginManifestRoom)

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/zip"
                            putExtra(Intent.EXTRA_STREAM, shareLink)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        context.startActivity(Intent.createChooser(shareIntent, "Share plugin"))
                    },
                    onWebUiButtonClick = {

                        val webUiUri = pluginScreenViewModel.getPluginWebUiUri(pluginManifestRoom)

                        val webUiIntent = Intent(context, WebViewActivity::class.java).apply {
                            putExtra("webUri",webUiUri)
                        }
                        context.startActivity(webUiIntent)
                    },
                    onDeleteButtonClick = { pluginScreenViewModel.uninstallPlugin(pluginManifestRoom) },
                    onBackButtonClick = { actionCanSee = !actionCanSee },
                    modifier = Modifier
                        .size(
                            width = with(density) { cardSize.width.toDp() },
                            height = with(density) { cardSize.height.toDp() }
                        )
                )

            }else{

                PluginInfoContainerLocal(
                    pluginManifestRoom = pluginManifestRoom,
                    onSwitchClick = {
                        pluginScreenViewModel.setPluginEnabled(
                            pluginID = pluginManifestRoom.pluginID,
                            pluginEnabledStatus = !pluginManifestRoom.enabled
                        )

                        if (!pluginManifestRoom.enabled){
                            navigateToExecuteScreen(pluginManifestRoom.pluginID,true)
                        }else{
                            coroutineScope.launch {
                                onAbortOnePlugin(pluginManifestRoom.pluginID)
                            }
                        }
                    },
                    onButtonClick = { onButtonClick(pluginManifestRoom.pluginID) },
                    onCardClick = {
                        if (pluginManifestRoom.enabled){
                            navigateToExecuteScreen(pluginManifestRoom.pluginID,false)
                        }
                    },
                    onCardLongClick = { actionCanSee = !actionCanSee },
                    onCardSizeChanged = { cardSize = it },
                )

            }
        }
    }
}