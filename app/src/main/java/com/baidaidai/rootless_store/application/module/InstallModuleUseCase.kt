package com.baidaidai.rootless_store.application.module

import android.net.Uri
import com.baidaidai.rootless_store.application.environment.InstallEnvironmentUseCase
import com.baidaidai.rootless_store.application.plugin.InstallMagiskPluginUseCase
import com.baidaidai.rootless_store.application.plugin.InstallPluginUseCase
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.model.LocalManifest
import javax.inject.Inject

class InstallModuleUseCase @Inject constructor(
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl,
    private val installPluginUseCase: InstallPluginUseCase,
    private val installEnvironmentUseCase: InstallEnvironmentUseCase,
    private val installMagiskPluginUseCase: InstallMagiskPluginUseCase
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        return when (judgeModule(uri)) {
            LocalManifest.PluginManifestLocal -> installPluginUseCase(uri)
            LocalManifest.EnvironmentManifestLocal -> installEnvironmentUseCase(uri)
            LocalManifest.MagiskProp -> installMagiskPluginUseCase(uri)
            null -> PluginError(
                errorMessage = "Neither PluginManifest.json nor EnvironmentManifest.json was found.",
                errorCause = "Unsupported module package: $uri"
            )
        }
    }

    private fun judgeModule(uri: Uri): LocalManifest? {
        val pluginManifestJson = androidFileSystemReadOperatorGatewayImpl.loadRawPluginManifest(uri)
        if (pluginManifestJson.isNotBlank()) {
            return LocalManifest.PluginManifestLocal
        }

        val environmentManifestJson = androidFileSystemReadOperatorGatewayImpl.loadRawEnvironmentManifest(uri)
        if (environmentManifestJson.isNotBlank()) {
            return LocalManifest.EnvironmentManifestLocal
        }

        val magiskModuleProp = androidFileSystemReadOperatorGatewayImpl.loadRawMagiskModuleProp(uri)
        if (magiskModuleProp.isNotBlank()) {
            return LocalManifest.MagiskProp
        }

        return null
    }
}
