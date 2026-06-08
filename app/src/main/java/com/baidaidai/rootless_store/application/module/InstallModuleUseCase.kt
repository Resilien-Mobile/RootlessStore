package com.baidaidai.rootless_store.application.module

import android.net.Uri
import com.baidaidai.rootless_store.application.environment.InstallOneEnvironmentUseCase
import com.baidaidai.rootless_store.application.plugin.InstallOnePluginUseCase
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.model.LocalManifest
import javax.inject.Inject

class InstallModuleUseCase @Inject constructor(
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val installOnePluginUseCase: InstallOnePluginUseCase,
    private val installOneEnvironmentUseCase: InstallOneEnvironmentUseCase
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        return when (judgeModule(uri)) {
            LocalManifest.PluginManifestLocal -> installOnePluginUseCase(uri)
            LocalManifest.EnvironmentManifestLocal -> installOneEnvironmentUseCase(uri)
            null -> PluginError(
                errorMessage = "Neither PluginManifest.json nor EnvironmentManifest.json was found.",
                errorCause = "Unsupported module package: $uri"
            )
        }
    }

    private fun judgeModule(uri: Uri): LocalManifest? {
        val pluginManifestJson = androidFileSystemCapabilityGatewayImpl.readRawPluginManifest(uri)
        if (pluginManifestJson.isNotBlank()) {
            return LocalManifest.PluginManifestLocal
        }

        val environmentManifestJson = androidFileSystemCapabilityGatewayImpl.readRawEnvironmentManifest(uri)
        if (environmentManifestJson.isNotBlank()) {
            return LocalManifest.EnvironmentManifestLocal
        }

        return null
    }
}
