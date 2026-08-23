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
import com.baidaidai.rootless_store.ui.components.marketScreen.MarketManifestCard
import com.baidaidai.rootless_store.ui.model.RootlessStoreMarketScreenViewModel

@Composable
fun MarketScreen(
    contentPadding: PaddingValues,
    marketScreenViewModel: RootlessStoreMarketScreenViewModel,
    navigateToPluginScreen: () -> Unit
){

    val listContentPadding = PaddingValues(vertical = 15.dp)

    val marketManifests = marketScreenViewModel.marketManifests.collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier
            .padding(contentPadding)
            .padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = listContentPadding
    ) {
        items(
            count = marketManifests.itemCount
        ){ manifestIndex ->

            val marketManifest = marketManifests[manifestIndex]
            MarketManifestCard(
                manifest = marketManifest!!
            ) {
                marketScreenViewModel.installMarketManifest(marketManifest)
                navigateToPluginScreen()
            }

        }
    }
}
