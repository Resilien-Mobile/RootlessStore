package com.baidaidai.rootless_store.data.plugin.module

import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.gateway.PluginGateway
import com.baidaidai.rootless_store.domain.plugin.repository.PluginRepository
import com.baidaidai.rootless_store.domain.plugin.repository.PluginStatusRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvidePluginModule {

    @Binds
    abstract fun ProvidePluginGateway(
        pluginGatewayImpl: PluginGatewayImpl
    ): PluginGateway

    @Binds
    abstract fun ProvidePluginRepository(
        pluginRepositoryImpl: PluginRepositoryImpl
    ): PluginRepository

    @Binds
    abstract fun ProvidePluginStatusRepository(
        pluginStatusRepositoryImpl: PluginStatusRepositoryImpl
    ): PluginStatusRepository

}
