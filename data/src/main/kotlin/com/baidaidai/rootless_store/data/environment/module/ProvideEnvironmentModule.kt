package com.baidaidai.rootless_store.data.environment.module

import com.baidaidai.rootless_store.data.environment.gateway.EnvironmentGatewayImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.environment.gateway.EnvironmentGateway
import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentRepository
import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentStatusRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideEnvironmentModule {

    @Binds
    abstract fun ProvideEnvironmentGateway(
        environmentGatewayImpl: EnvironmentGatewayImpl
    ): EnvironmentGateway

    @Binds
    abstract fun ProvideEnvironmentRepository(
        environmentRepositoryImpl: EnvironmentRepositoryImpl
    ): EnvironmentRepository

    @Binds
    abstract fun ProvideEnvironmentStatusRepository(
        environmentStatusRepositoryImpl: EnvironmentStatusRepositoryImpl
    ): EnvironmentStatusRepository

}
