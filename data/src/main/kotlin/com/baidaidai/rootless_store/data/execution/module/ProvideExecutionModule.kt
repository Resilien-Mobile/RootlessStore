package com.baidaidai.rootless_store.data.execution.module

import com.baidaidai.rootless_store.data.execution.gateway.PluginExecutionGatewayImpl
import com.baidaidai.rootless_store.data.execution.repository.PluginExecutionRepositoryImpl
import com.baidaidai.rootless_store.domain.execution.gateway.PluginExecutionGateway
import com.baidaidai.rootless_store.domain.execution.repository.PluginExecutionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideExecutionModule {

    @Binds
    abstract fun ProvidePluginExecutionGateway(
        pluginExecutionGatewayImpl: PluginExecutionGatewayImpl
    ): PluginExecutionGateway

    @Binds
    abstract fun ProvidePluginExecutionRepository(
        pluginExecutionRepositoryImpl: PluginExecutionRepositoryImpl
    ): PluginExecutionRepository

}
