package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
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
        val pluginManifest: PluginManifest = pluginGatewayImpl
            .parsePluginManifest(uri)
            .getOrElse { throwable ->
                return PluginError(
                    errorMessage = "Can't parse plugin manifest",
                    errorCause = "Wrong type of PluginManifest.json \n\n" + throwable.stackTrace.formatAsMultilineString()
                )
            }

        if (pluginManifest.requiredEnvironment == ExecutionContext.ADB){
            return installShellPluginUseCase(uri)
        }else{

            // Un-Zip, Install Plugin
            pluginGatewayImpl
                .installPluginFromLocal(uri)
                .onFailure { throwable ->
                    return PluginError(
                        errorMessage = "Can't Un-Zip / install Plugin",
                        errorCause = "Maybe storage permission denied \n\n" + throwable.stackTrace.formatAsMultilineString()
                    )
                }

            // Set Execute-able, Made it can call and use
            pluginGatewayImpl
                .setPluginEntryPointExecutable(pluginManifest)
                .onFailure { throwable ->
                    return PluginError(
                        errorMessage = "Can't set plugin executable",
                        errorCause = "Please check your shell permission \n\n" + throwable.stackTrace.formatAsMultilineString()
                    )
                }

            // Add Data, Register Plugin
            pluginRepositoryImpl.addPlugin(pluginManifest)
            pluginStatusRepositoryImpl.registerPluginStatus(pluginManifest.pluginId, PluginOrigin.Local)

            return null
        }
    }
}
