package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.components.pluginsScreen.PluginInfoContainerLocal
import com.baidaidai.rootless_store.ui.model.RootLessStorePluginScreenViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.ui.model.RootLessStoreExecuteScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun RootlessStorePluginScreenContainer(
    contentPadding: PaddingValues,
    pluginScreenViewModel: RootLessStorePluginScreenViewModel,
//    executeScreenViewModel: RootLessStoreExecuteScreenViewModel,
    navigateToExecuteScreen: (pluginID: String,isExecutePlugin: Boolean)-> Unit,
    onAbortOnePlugin:suspend (pluginID: String) -> Unit
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
//                    executeScreenViewModel = executeScreenViewModel,
                    pluginScreenViewModel = pluginScreenViewModel,
                    navigateToExecuteScreen = navigateToExecuteScreen,
                    onAbortOnePlugin = onAbortOnePlugin
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
//    executeScreenViewModel: RootLessStoreExecuteScreenViewModel,
    pluginScreenViewModel: RootLessStorePluginScreenViewModel,
    navigateToExecuteScreen: (pluginID: String,isExecutePlugin: Boolean)-> Unit,
    onAbortOnePlugin: suspend (pluginID: String) -> Unit
){
    val coroutineScope = rememberCoroutineScope()

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
                pluginManifest = it,
                onSwitchClick = {
                    pluginScreenViewModel.setPluginEnabled(
                        pluginID = it.pluginID,
                        pluginEnabledStatus = !it.enabled
                    )

                    if (!it.enabled){
                        navigateToExecuteScreen(it.pluginID,true)
                    }else{
                        coroutineScope.launch {
                            onAbortOnePlugin(it.pluginID)
                        }
                    }
                },
                onBadgeClick = {
                    pluginScreenViewModel.uninstallPlugin(it)
                },
                onCardClick = {
                    if (it.enabled){
                        navigateToExecuteScreen(it.pluginID,false)
                    }
                },
                badgeShowState = badgeShowState
            )
        }
    }
}