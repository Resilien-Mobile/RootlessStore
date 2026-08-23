package com.baidaidai.rootless_store.domain.market.usecase

import androidx.paging.PagingData
import com.baidaidai.rootless_store.data.market.gateway.PluginMarketGatewayImpl
import com.baidaidai.rootless_store.domain.market.error.MarketError
import com.baidaidai.rootless_store.domain.module.model.ModuleManifestCollection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchRemotePluginsUseCase @Inject constructor(
    private val pluginMarketGatewayImpl: PluginMarketGatewayImpl
) {
    operator fun invoke(
        pluginSourceUri: String,
        onError: suspend (MarketError)-> Unit
    ): Flow<PagingData<ModuleManifestCollection>> {
       return pluginMarketGatewayImpl.fetchPlugins(pluginSourceUri,onError)
    }
}
