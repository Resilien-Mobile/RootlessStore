package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import javax.inject.Inject

class InstallOnePluginUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginGatewayImpl: PluginGatewayImpl,
    private val installOneShellPluginUseCase: InstallOneShellPluginUseCase
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        return try {
            val pluginManiFestLocal = pluginGatewayImpl.parsePluginManifest(uri)

            if (pluginManiFestLocal.requiredEnvironment == HosterOverallStatus.ADB){
                installOneShellPluginUseCase(uri)
            }else{
                // Un-Zip, Install Plugin
                pluginGatewayImpl.installPluginFromLocal(uri)
                // Set Execute-able, Made it can call and use
                pluginGatewayImpl.setPluginEntryPointExecutable(pluginManiFestLocal)
                // Add Data, Register Plugin
                pluginRepositoryImpl.insertOnePluginInfo(pluginManiFestLocal)

                null
            }
        }catch (error: Throwable){
            PluginError(
                "",
                ""
            )
        }
    }
}