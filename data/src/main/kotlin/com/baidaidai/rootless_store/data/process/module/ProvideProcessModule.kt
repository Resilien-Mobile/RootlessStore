package com.baidaidai.rootless_store.data.process.module

import com.baidaidai.rootless_store.data.process.gateway.ProcessGatewayImpl
import com.baidaidai.rootless_store.domain.process.gateway.ProcessGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideProcessModule {

    @Binds
    abstract fun ProvideProcessGateway(
        processGatewayImpl: ProcessGatewayImpl
    ): ProcessGateway

}
