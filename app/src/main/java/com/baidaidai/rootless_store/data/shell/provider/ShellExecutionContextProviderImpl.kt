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
        val environmentPATH = environmentRepositoryImpl.resolveEnvironmentPath()
        val environmentLDPATH = environmentRepositoryImpl.resolveEnvironmentLdPath()
        val environmentConfig = environmentRepositoryImpl.resolveEnvironmentConfig()

        return AppShellContextConfig(
            jumpToDirectory = preferences.jumpToDirectory,
            environmentPATH = environmentPATH,
            environmentLDPATH = environmentLDPATH,
            environmentConfig = environmentConfig,
        )
    }

    suspend fun resolveAdbShellContext(): AdbShellContextConfig{
        val preferences = shellPreferencesRepositoryImpl.shellContextPreferences.first()
        val environmentPATH = environmentRepositoryImpl.resolveEnvironmentPath()
        val environmentLDPATH = environmentRepositoryImpl.resolveEnvironmentLdPath()
        val environmentConfig = environmentRepositoryImpl.resolveEnvironmentConfig()
        val environmentConfigKeyList = environmentRepositoryImpl.resolveEnvironmentConfigKeys()
        val environmentConfigValueList = environmentRepositoryImpl.resolveEnvironmentConfigValues()

        return AdbShellContextConfig(
            jumpToDirectory = preferences.jumpToDirectory,
            environmentPATH = environmentPATH,
            environmentLDPATH = environmentLDPATH,
            environmentConfig = environmentConfig,
            environmentConfigKeyList = environmentConfigKeyList,
            environmentConfigValueList = environmentConfigValueList,
        )
    }

    fun resolveRootShellContext(){

    }

}
