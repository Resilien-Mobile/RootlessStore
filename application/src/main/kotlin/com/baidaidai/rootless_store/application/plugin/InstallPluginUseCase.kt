package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import javax.inject.Inject

class InstallPluginUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginGatewayImpl: PluginGatewayImpl,
    private val installShellPluginUseCase: com.baidaidai.rootless_store.application.plugin.InstallShellPluginUseCase
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        return try {
            val pluginManifestLocal = pluginGatewayImpl.parsePluginManifest(uri)

            if (pluginManifestLocal.requiredEnvironment == ExecutionContext.ADB){
                installShellPluginUseCase(uri)
            }else{
                // Un-Zip, Install Plugin
                pluginGatewayImpl.installPluginFromLocal(uri)
                // Set Execute-able, Made it can call and use
                pluginGatewayImpl.setPluginEntryPointExecutable(pluginManifestLocal)
                // Add Data, Register Plugin
                pluginRepositoryImpl.addPlugin(pluginManifestLocal)

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
