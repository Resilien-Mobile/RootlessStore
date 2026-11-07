package com.baidaidai.rootless_store.data.shell.module

import com.baidaidai.rootless_store.data.shell.gateway.ExecuteShellGatewayImpl
import com.baidaidai.rootless_store.data.shell.repository.ShellPreferencesRepositoryImpl
import com.baidaidai.rootless_store.domain.shell.gateway.ExecuteShellGateway
import com.baidaidai.rootless_store.domain.shell.repository.ShellPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideShellModule {

    @Binds
    abstract fun ProvideExecuteShellGateway(
        executeShellGatewayImpl: ExecuteShellGatewayImpl
    ): ExecuteShellGateway

    @Binds
    abstract fun ProvideShellPreferencesRepository(
        shellPreferencesRepositoryImpl: ShellPreferencesRepositoryImpl
    ): ShellPreferencesRepository

}
