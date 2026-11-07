package com.baidaidai.rootless_store.domain.market.gateway

import androidx.paging.PagingData
import com.baidaidai.rootless_store.domain.market.error.MarketError
import com.baidaidai.rootless_store.domain.market.model.MarketManifest
import kotlinx.coroutines.flow.Flow

interface MarketGateway {

    fun fetchMarketManifests(
        pluginSourceEndpoint: String,
        onError: suspend (MarketError) -> Unit
    ): Flow<PagingData<MarketManifest>>

}
