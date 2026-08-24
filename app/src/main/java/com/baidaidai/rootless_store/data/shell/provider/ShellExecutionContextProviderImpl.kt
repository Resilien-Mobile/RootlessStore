package com.baidaidai.rootless_store.data.shell.provider

import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.shell.repository.ShellPreferencesRepositoryImpl
import com.baidaidai.rootless_store.domain.shell.model.AdbShellContextConfig
import com.baidaidai.rootless_store.domain.shell.model.AppShellContextConfig
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ShellExecutionContextProviderImpl @Inject constructor(
    private val shellPreferencesRepositoryImpl: ShellPreferencesRepositoryImpl,
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl
) {

    suspend fun resolveAppShellContext(): AppShellContextConfig{
        val preferences = shellPreferencesRepositoryImpl.shellContextPreferences.first()
        val environmentPath = environmentRepositoryImpl.resolveEnvironmentRuntimePath()
        val environmentLdPath = environmentRepositoryImpl.resolveEnvironmentLdPath()
        val environmentConfig = environmentRepositoryImpl.resolveEnvironmentConfig()

        return AppShellContextConfig(
            shouldJumpToDirectory = preferences.shouldJumpToDirectory,
            environmentPath = environmentPath,
            environmentLdPath = environmentLdPath,
            environmentConfig = environmentConfig,
        )
    }

    suspend fun resolveAdbShellContext(): AdbShellContextConfig{
        val preferences = shellPreferencesRepositoryImpl.shellContextPreferences.first()
        val environmentPath = environmentRepositoryImpl.resolveEnvironmentRuntimePath()
        val environmentLdPath = environmentRepositoryImpl.resolveEnvironmentLdPath()
        val environmentConfig = environmentRepositoryImpl.resolveEnvironmentConfig()
        val environmentConfigKeys = environmentRepositoryImpl.resolveEnvironmentConfigKeys()
        val environmentConfigValues = environmentRepositoryImpl.resolveEnvironmentConfigValues()

        return AdbShellContextConfig(
            shouldJumpToDirectory = preferences.shouldJumpToDirectory,
            environmentPath = environmentPath,
            environmentLdPath = environmentLdPath,
            environmentConfig = environmentConfig,
            environmentConfigKeys = environmentConfigKeys,
            environmentConfigValues = environmentConfigValues,
        )
    }

    fun resolveRootShellContext(){

    }

}
