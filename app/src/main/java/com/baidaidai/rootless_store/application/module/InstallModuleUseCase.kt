package com.baidaidai.rootless_store.application.module

import android.net.Uri
import com.baidaidai.rootless_store.application.environment.InstallOneEnvironmentUseCase
import com.baidaidai.rootless_store.application.plugin.InstallOneMagiskPluginUseCase
import com.baidaidai.rootless_store.application.plugin.InstallOnePluginUseCase
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.model.LocalManifest
import javax.inject.Inject

class InstallModuleUseCase @Inject constructor(
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl,
    private val installOnePluginUseCase: InstallOnePluginUseCase,
    private val installOneEnvironmentUseCase: InstallOneEnvironmentUseCase,
    private val installOneMagiskPluginUseCase: InstallOneMagiskPluginUseCase
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        return when (judgeModule(uri)) {
            LocalManifest.PluginManifestLocal -> installOnePluginUseCase(uri)
            LocalManifest.EnvironmentManifestLocal -> installOneEnvironmentUseCase(uri)
            LocalManifest.MagiskProp -> installOneMagiskPluginUseCase(uri)
            null -> PluginError(
                errorMessage = "Neither PluginManifest.json nor EnvironmentManifest.json was found.",
                errorCause = "Unsupported module package: $uri"
            )
        }
    }

    private fun judgeModule(uri: Uri): LocalManifest? {
        val pluginManifestJson = androidFileSystemReadOperatorGatewayImpl.readRawPluginManifest(uri)
        if (pluginManifestJson.isNotBlank()) {
            return LocalManifest.PluginManifestLocal
        }

        val environmentManifestJson = androidFileSystemReadOperatorGatewayImpl.readRawEnvironmentManifest(uri)
        if (environmentManifestJson.isNotBlank()) {
            return LocalManifest.EnvironmentManifestLocal
        }

        val magiskModuleProp = androidFileSystemReadOperatorGatewayImpl.readRawMagiskModuleProp(uri)
        if (magiskModuleProp.isNotBlank()) {
            return LocalManifest.MagiskProp
        }

        return null
    }
}
