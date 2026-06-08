package com.baidaidai.rootless_store.data.market.gateway

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.baidaidai.rootless_store.data.market.paging.PluginPagingSource
import com.baidaidai.rootless_store.data.market.remote.api.PluginMarketAPI
import com.baidaidai.rootless_store.domain.market.error.MarketError
import com.baidaidai.rootless_store.domain.module.model.ModuleManifestCollection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PluginMarketGatewayImpl @Inject constructor(
    private val api: PluginMarketAPI
) {
    fun getPlugins(
        pluginSourceUri: String,
        onError: suspend (MarketError)-> Unit
    ): Flow<PagingData<ModuleManifestCollection>> {
        return Pager(
            // Rule of Paging
            config = PagingConfig(
                pageSize = 10,
            ),

            // Core & Action of Paging
            pagingSourceFactory = {
                PluginPagingSource(
                    api = api,
                    pluginSourceUri = pluginSourceUri,
                    onError = onError
                )
            }
        ).flow
    }
}