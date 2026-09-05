package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import javax.inject.Inject

class InstallPluginUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl,
    private val pluginGatewayImpl: PluginGatewayImpl,
    private val installShellPluginUseCase: InstallShellPluginUseCase
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        return try {
            val pluginManifest = pluginGatewayImpl.parsePluginManifest(uri)

            if (pluginManifest.requiredEnvironment == ExecutionContext.ADB){
                installShellPluginUseCase(uri)
            }else{
                // Un-Zip, Install Plugin
                pluginGatewayImpl.installPluginFromLocal(uri)

                // Set Execute-able, Made it can call and use
                pluginGatewayImpl.setPluginEntryPointExecutable(pluginManifest)

                // Add Data, Register Plugin
                pluginRepositoryImpl.addPlugin(pluginManifest)
                pluginStatusRepositoryImpl.registerPluginStatus(pluginManifest.pluginId, PluginOrigin.Local)

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
