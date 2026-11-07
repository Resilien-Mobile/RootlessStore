package com.baidaidai.rootless_store.data.update.module

import com.baidaidai.rootless_store.data.update.gateway.StoreUpdateGatewayImpl
import com.baidaidai.rootless_store.domain.update.gateway.StoreUpdateGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideUpdateModule {

    @Binds
    abstract fun ProvideStoreUpdateGateway(
        storeUpdateGatewayImpl: StoreUpdateGatewayImpl
    ): StoreUpdateGateway

}
