package com.baidaidai.rootless_store.data.source.module

import com.baidaidai.rootless_store.data.source.gateway.PluginSourceGatewayImpl
import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import com.baidaidai.rootless_store.domain.source.gateway.PluginSourceGateway
import com.baidaidai.rootless_store.domain.source.repository.PluginSourceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideSourceModule {

    @Binds
    abstract fun ProvidePluginSourceGateway(
        pluginSourceGatewayImpl: PluginSourceGatewayImpl
    ): PluginSourceGateway

    @Binds
    abstract fun ProvidePluginSourceRepository(
        pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
    ): PluginSourceRepository

}
