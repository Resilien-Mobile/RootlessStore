package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReZipOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemSearchOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemShareOperatorGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import java.io.File
import javax.inject.Inject

class ResolvePluginShareUriUseCase @Inject constructor(
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl,
    private val androidFileSystemSearchOperatorGatewayImpl: AndroidFileSystemSearchOperatorGatewayImpl,
    private val androidFileSystemCreateOperatorGatewayImpl: AndroidFileSystemCreateOperatorGatewayImpl,
    private val androidFileSystemReZipOperatorGatewayImpl: AndroidFileSystemReZipOperatorGatewayImpl,
    private val androidFileSystemShareOperatorGatewayImpl: AndroidFileSystemShareOperatorGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl
) {
    operator fun invoke(
        pluginManifestRoom: PluginManifestRoom
    ): Uri {

        if (pluginManifestRoom.requiredEnvironment == HosterOverallStatus.ADB) {
            return resolveShellPluginShareUri(pluginManifestRoom)
        }

        val defaultPluginCacheDirectory = androidFileSystemDefaultOperatorGatewayImpl.getCachePluginDirectoryFile()

        // Get plugin package directory -> /file/Plugin/PLUGIN_DIRECTORY
        val pluginPackageDirectory = androidFileSystemDefaultOperatorGatewayImpl.resolvePluginPackageDirectory(pluginManifestRoom)

        // Detect if cache/plugin available -> /cache/Plugin
        val pluginCacheDirectoryAvailable = androidFileSystemSearchOperatorGatewayImpl.confirmPluginCacheExists()

        if (!pluginCacheDirectoryAvailable){
            androidFileSystemCreateOperatorGatewayImpl.createCacheDir("Plugin")
        }

        // Zip to cache/plugin
        // Create void zip content -> /cache/Plugin/PLUGIN.zip
        val zipFile = androidFileSystemCreateOperatorGatewayImpl.createVoidFileDirectory(defaultPluginCacheDirectory,"${pluginManifestRoom.pluginPackageName}.zip")

        // Write Zip Byte
        androidFileSystemReZipOperatorGatewayImpl.rezipFromFile(
            originPluginFile = File(pluginPackageDirectory),
            targetZipFile = zipFile
        )

        // Check if Zip is really available (option)
//      TODO("Check Zip integrity")

        // Convert from zip to ShareLink
        val shareLink = androidFileSystemShareOperatorGatewayImpl.resolveShareUriFromFile(zipFile)

        return shareLink

    }

    private fun resolveShellPluginShareUri(
        pluginManifestRoom: PluginManifestRoom
    ): Uri {

        // Get shell plugin export cache zip -> /storage/emulated/0/Android/data/com.baidaidai.rootless_store/cache/PLUGIN.zip
        val shellPluginExportCacheDirectory = androidFileSystemDefaultOperatorGatewayImpl.getExternalAppCacheDirectoryFile()
        val shellPluginExportZipFile = File(
            shellPluginExportCacheDirectory,
            "${pluginManifestRoom.pluginPackageName}.zip"
        )

        // Shizuku File Flow
        val shellPluginExportResult = shizukuUserServiceGatewayImpl.getShizukuUserService()
            ?.exportShellPlugin(
                pluginManifestRoom.pluginPackageName,
                shellPluginExportZipFile.path
            ) ?: false

        if (!shellPluginExportResult) {
            throw IllegalStateException("Failed to export shell plugin. pluginPackageName=${pluginManifestRoom.pluginPackageName}")
        }

        // Check if Zip is really available (option)
//      TODO("Check Zip integrity")

        // Convert from zip to ShareLink
        val shareLink = androidFileSystemShareOperatorGatewayImpl.resolveShareUriFromFile(shellPluginExportZipFile)

        return shareLink

    }
}
