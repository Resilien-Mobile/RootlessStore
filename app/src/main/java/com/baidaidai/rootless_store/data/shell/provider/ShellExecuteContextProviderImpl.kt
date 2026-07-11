package com.baidaidai.rootless_store.data.shell.provider

import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.shell.repository.ShellPreferencesRepositoryImpl
import com.baidaidai.rootless_store.domain.shell.model.AdbShellContextConfig
import com.baidaidai.rootless_store.domain.shell.model.AppShellContextConfig
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ShellExecuteContextProviderImpl @Inject constructor(
    private val shellPreferencesRepositoryImpl: ShellPreferencesRepositoryImpl,
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl
) {

    suspend fun getAppShellContext(): AppShellContextConfig{
        val preferences = shellPreferencesRepositoryImpl.shellContextPreferences.first()
        val environmentPATH = environmentRepositoryImpl.getAvailableEnvironmentPath()
        val environmentLDPATH = environmentRepositoryImpl.getAvailableEnvironmentLDPATH()
        val environmentConfig = environmentRepositoryImpl.getAvailableEnvironmentConfig()

        return AppShellContextConfig(
            jumpToDirectory = preferences.jumpToDirectory,
            environmentPATH = environmentPATH,
            environmentLDPATH = environmentLDPATH,
            environmentConfig = environmentConfig,
        )
    }

    suspend fun getAdbShellContext(): AdbShellContextConfig{
        val preferences = shellPreferencesRepositoryImpl.shellContextPreferences.first()
        val environmentPATH = environmentRepositoryImpl.getAvailableEnvironmentPath()
        val environmentLDPATH = environmentRepositoryImpl.getAvailableEnvironmentLDPATH()
        val environmentConfig = environmentRepositoryImpl.getAvailableEnvironmentConfig()
        val environmentConfigKeyList = environmentRepositoryImpl.getEnvironmentConfigKeyList()
        val environmentConfigValueList = environmentRepositoryImpl.getEnvironmentConfigValueList()

        return AdbShellContextConfig(
            jumpToDirectory = preferences.jumpToDirectory,
            environmentPATH = environmentPATH,
            environmentLDPATH = environmentLDPATH,
            environmentConfig = environmentConfig,
            environmentConfigKeyList = environmentConfigKeyList,
            environmentConfigValueList = environmentConfigValueList,
        )
    }

    fun getRootShellContext(){

    }

}
