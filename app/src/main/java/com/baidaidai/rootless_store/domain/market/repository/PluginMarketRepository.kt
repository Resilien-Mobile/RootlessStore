package com.baidaidai.rootless_store.domain.market.repository

import androidx.paging.PagingData
import com.baidaidai.rootless_store.domain.market.error.MarketError
import com.baidaidai.rootless_store.domain.plugin.manifest.RootlessStoreManifestCollection
import kotlinx.coroutines.flow.Flow

interface PluginMarketRepository {
    fun getPlugins(
        pluginSourceUri: String,
        onError: suspend (MarketError)-> Unit
    ): Flow<PagingData<RootlessStoreManifestCollection>>
}