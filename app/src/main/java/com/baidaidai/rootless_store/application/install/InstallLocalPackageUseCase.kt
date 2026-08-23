package com.baidaidai.rootless_store.application.install

import android.net.Uri
import com.baidaidai.rootless_store.application.environment.InstallEnvironmentUseCase
import com.baidaidai.rootless_store.application.plugin.InstallMagiskPluginUseCase
import com.baidaidai.rootless_store.application.plugin.InstallPluginUseCase
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.install.model.LocalPackageType
import javax.inject.Inject

class InstallLocalPackageUseCase @Inject constructor(
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl,
    private val installPluginUseCase: InstallPluginUseCase,
    private val installEnvironmentUseCase: InstallEnvironmentUseCase,
    private val installMagiskPluginUseCase: InstallMagiskPluginUseCase
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        return when (resolveLocalPackageType(uri)) {
            LocalPackageType.Plugin -> installPluginUseCase(uri)
            LocalPackageType.Environment -> installEnvironmentUseCase(uri)
            LocalPackageType.MagiskModule -> installMagiskPluginUseCase(uri)
            null -> PluginError(
                errorMessage = "Neither PluginManifest.json nor EnvironmentManifest.json was found.",
                errorCause = "Unsupported local package: $uri"
            )
        }
    }

    private fun resolveLocalPackageType(uri: Uri): LocalPackageType? {
        val pluginManifestJson = androidFileSystemReadOperatorGatewayImpl.loadRawPluginManifest(uri)
        if (pluginManifestJson.isNotBlank()) {
            return LocalPackageType.Plugin
        }

        val environmentManifestJson = androidFileSystemReadOperatorGatewayImpl.loadRawEnvironmentManifest(uri)
        if (environmentManifestJson.isNotBlank()) {
            return LocalPackageType.Environment
        }

        val magiskModuleProp = androidFileSystemReadOperatorGatewayImpl.loadRawMagiskModuleProp(uri)
        if (magiskModuleProp.isNotBlank()) {
            return LocalPackageType.MagiskModule
        }

        return null
    }
}
