package com.baidaidai.rootless_store.data.market.gateway

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.baidaidai.rootless_store.data.market.paging.MarketManifestPagingSource
import com.baidaidai.rootless_store.data.market.remote.api.MarketApi
import com.baidaidai.rootless_store.domain.market.error.MarketError
import com.baidaidai.rootless_store.domain.market.model.MarketManifest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MarketGatewayImpl @Inject constructor(
    private val marketApi: MarketApi
) {
    fun fetchMarketManifests(
        pluginSourceEndpoint: String,
        onError: suspend (MarketError)-> Unit
    ): Flow<PagingData<MarketManifest>> {
        return Pager(
            // Rule of Paging
            config = PagingConfig(
                pageSize = 10,
            ),

            // Core & Action of Paging
            pagingSourceFactory = {
                MarketManifestPagingSource(
                    marketApi = marketApi,
                    pluginSourceEndpoint = pluginSourceEndpoint,
                    onError = onError
                )
            }
        ).flow
    }
}
