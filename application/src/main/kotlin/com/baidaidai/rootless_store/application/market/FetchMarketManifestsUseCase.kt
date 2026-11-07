package com.baidaidai.rootless_store.application.market

import androidx.paging.PagingData
import com.baidaidai.rootless_store.domain.market.gateway.MarketGateway
import com.baidaidai.rootless_store.domain.market.error.MarketError
import com.baidaidai.rootless_store.domain.market.model.MarketManifest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchMarketManifestsUseCase @Inject constructor(
    private val marketGateway: MarketGateway
) {
    operator fun invoke(
        pluginSourceEndpoint: String,
        onError: suspend (MarketError)-> Unit
    ): Flow<PagingData<MarketManifest>> {
       return marketGateway.fetchMarketManifests(pluginSourceEndpoint,onError)
    }
}