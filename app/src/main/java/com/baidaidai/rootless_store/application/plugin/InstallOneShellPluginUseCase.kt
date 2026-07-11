package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.core.util.OutOfStringLike
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import java.io.File
import javax.inject.Inject

class InstallOneShellPluginUseCase @Inject constructor(
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val pluginGatewayImpl: PluginGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        return try {

            // Parse PluginManifestLocal
            val pluginManifestLocal = pluginGatewayImpl.parsePluginManifest(uri)

            // Copy to /sdcard/RootlessStore
            val shellPluginStagingDirectory = androidFileSystemCapabilityGatewayImpl.getShellPluginStagingDirectoryFile()
            val shellPluginStagingFile = File(shellPluginStagingDirectory, "_template_.zip")

            androidFileSystemCapabilityGatewayImpl.copyUriToFile(
                originFileURI = uri,
                targetFile = shellPluginStagingFile
            )

            // Shizuku File Flow
            val shellPluginInstallResult = shizukuUserServiceGatewayImpl.getShizukuUserService()
                ?.installShellPlugin(
                    pluginManifestLocal.pluginPackageName,
                    pluginManifestLocal.entryPoint
                ) ?: false

            // Delete /sdcard/RootlessStore
            val deleteShellPluginStagingDirectoryResult = androidFileSystemCapabilityGatewayImpl.deleteFileOrDirectory(
                shellPluginStagingDirectory.path
            )

            if (!shellPluginInstallResult) {
                return PluginError(
                    errorMessage = "Install shell plugin failed",
                    errorCause = "Failed to copy shell plugin into com.android.shell private directory. pluginPackageName=${pluginManifestLocal.pluginPackageName}, entryPoint=${pluginManifestLocal.entryPoint}"
                )
            }

            if (!deleteShellPluginStagingDirectoryResult) {
                return PluginError(
                    errorMessage = "Delete shell plugin staging directory failed",
                    errorCause = "Failed to delete ${shellPluginStagingDirectory.path}"
                )
            }

            // Add Data, Register Plugin
            pluginRepositoryImpl.insertOnePluginInfo(pluginManifestLocal)

            null
        } catch (error: Throwable) {
            PluginError(
                errorMessage = error.message ?: "Install shell plugin crashed",
                errorCause = error.stackTrace.OutOfStringLike()
            )
        }
    }
}
