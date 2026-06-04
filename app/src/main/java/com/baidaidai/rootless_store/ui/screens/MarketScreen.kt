package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.baidaidai.rootless_store.components.marketScreen.PluginInfoContainerRemote
import com.baidaidai.rootless_store.ui.model.RootLessStoreMarketScreenViewModel

@Composable
fun MarketScreen(
    contentPadding: PaddingValues,
    marketScreenViewModel: RootLessStoreMarketScreenViewModel,
    navigateToPluginScreen: () -> Unit
){

    val _contentPadding = PaddingValues(vertical = 15.dp)

    val remotePluginList = marketScreenViewModel.remotePluginList.collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier
            .padding(contentPadding)
            .padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = _contentPadding
    ) {
        items(
            count = remotePluginList.itemCount
        ){ pluginIndex ->
            val remotePluginListContent = remotePluginList[pluginIndex]
            PluginInfoContainerRemote(
                manifest = remotePluginListContent!!
            ) {
                marketScreenViewModel.installPlugin(remotePluginListContent)
                navigateToPluginScreen()
            }
        }
    }
}