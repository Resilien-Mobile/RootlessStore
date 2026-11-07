package com.baidaidai.rootless_store.data.status.module

import com.baidaidai.rootless_store.data.status.gateway.StoreStatusGatewayImpl
import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.gateway.StoreStatusGateway
import com.baidaidai.rootless_store.domain.status.repository.StoreStatusRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideStatusModule {

    @Binds
    abstract fun ProvideStoreStatusGateway(
        storeStatusGatewayImpl: StoreStatusGatewayImpl
    ): StoreStatusGateway

    @Binds
    abstract fun ProvideStoreStatusRepository(
        storeStatusRepositoryImpl: StoreStatusRepositoryImpl
    ): StoreStatusRepository

}
