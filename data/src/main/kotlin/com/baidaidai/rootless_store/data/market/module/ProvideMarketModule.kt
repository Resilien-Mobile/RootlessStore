package com.baidaidai.rootless_store.data.market.module

import com.baidaidai.rootless_store.data.market.gateway.MarketGatewayImpl
import com.baidaidai.rootless_store.domain.market.gateway.MarketGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideMarketModule {

    @Binds
    abstract fun ProvideMarketGateway(
        marketGatewayImpl: MarketGatewayImpl
    ): MarketGateway

}
