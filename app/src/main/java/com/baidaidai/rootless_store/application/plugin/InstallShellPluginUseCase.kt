package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDeleteOperatorGatewayImpl
import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import java.io.File
import javax.inject.Inject

class InstallShellPluginUseCase @Inject constructor(
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl,
    private val androidFileSystemCreateOperatorGatewayImpl: AndroidFileSystemCreateOperatorGatewayImpl,
    private val androidFileSystemDeleteOperatorGatewayImpl: AndroidFileSystemDeleteOperatorGatewayImpl,
    private val pluginGatewayImpl: PluginGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        return try {

            // Parse PluginManifestLocal
            val pluginManifestLocal = pluginGatewayImpl.parsePluginManifest(uri)

            // Copy to /storage/emulated/0/Android/data/com.baidaidai.rootless_store/files/_template_.zip
            val shellPluginStagingDirectory = androidFileSystemDefaultOperatorGatewayImpl.getExternalAppDirectoryPath()
            val shellPluginStagingFile = File(shellPluginStagingDirectory, "_template_.zip")

            androidFileSystemCreateOperatorGatewayImpl.copyUriToFile(
                originFileUri = uri,
                targetFile = shellPluginStagingFile
            )

            // Shizuku File Flow
            val shellPluginInstallResult = shizukuUserServiceGatewayImpl.findShizukuUserService()
                ?.installShellPlugin(
                    shellPluginStagingFile.path,
                    pluginManifestLocal.pluginPackageName,
                    pluginManifestLocal.entryPoint
                ) ?: false

            // Delete /storage/emulated/0/Android/data/com.baidaidai.rootless_store/files/_template_.zip
            val deleteShellPluginStagingFileResult = androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(
                shellPluginStagingFile.path
            )

            if (!shellPluginInstallResult) {
                return PluginError(
                    errorMessage = "Install shell plugin failed",
                    errorCause = "Failed to copy shell plugin into com.android.shell private directory. pluginPackageName=${pluginManifestLocal.pluginPackageName}, entryPoint=${pluginManifestLocal.entryPoint}"
                )
            }

            if (!deleteShellPluginStagingFileResult) {
                return PluginError(
                    errorMessage = "Delete shell plugin staging file failed",
                    errorCause = "Failed to delete ${shellPluginStagingFile.path}"
                )
            }

            // Add Data, Register Plugin
            pluginRepositoryImpl.addPlugin(pluginManifestLocal)

            null
        } catch (error: Throwable) {
            PluginError(
                errorMessage = error.message ?: "Install shell plugin crashed",
                errorCause = error.stackTrace.formatAsMultilineString()
            )
        }
    }
}
