package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemRezipOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemSearchOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemShareOperatorGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import java.io.File
import javax.inject.Inject

class ResolvePluginShareUriUseCase @Inject constructor(
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl,
    private val androidFileSystemSearchOperatorGatewayImpl: AndroidFileSystemSearchOperatorGatewayImpl,
    private val androidFileSystemCreateOperatorGatewayImpl: AndroidFileSystemCreateOperatorGatewayImpl,
    private val androidFileSystemRezipOperatorGatewayImpl: AndroidFileSystemRezipOperatorGatewayImpl,
    private val androidFileSystemShareOperatorGatewayImpl: AndroidFileSystemShareOperatorGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl
) {
    operator fun invoke(
        pluginManifestRoom: PluginManifestRoom
    ): Uri {

        if (pluginManifestRoom.requiredEnvironment == ExecutionContext.ADB) {
            return resolveShellPluginShareUri(pluginManifestRoom)
        }

        val internalPluginCacheDirectory = androidFileSystemDefaultOperatorGatewayImpl.getInternalPluginCacheDirectoryFile()

        // Get plugin package directory -> /file/Plugin/PLUGIN_DIRECTORY
        val pluginPackageDirectory = androidFileSystemDefaultOperatorGatewayImpl.resolvePluginPackageDirectory(pluginManifestRoom)

        // Detect if cache/plugin available -> /cache/Plugin
        val hasPluginCacheDirectory = androidFileSystemSearchOperatorGatewayImpl.hasPluginCacheDirectory()

        if (!hasPluginCacheDirectory){
            androidFileSystemCreateOperatorGatewayImpl.ensureCacheDirectory("Plugin")
        }

        // Zip to cache/plugin
        // Resolve zip target -> /cache/Plugin/PLUGIN.zip
        val zipFile = androidFileSystemCreateOperatorGatewayImpl.resolveChildFile(internalPluginCacheDirectory,"${pluginManifestRoom.pluginPackageName}.zip")

        // Write Zip Byte
        androidFileSystemRezipOperatorGatewayImpl.rezipFromFile(
            originPluginFile = File(pluginPackageDirectory),
            targetZipFile = zipFile
        )

        // Check if Zip is really available (option)
//      TODO("Check Zip integrity")

        // Convert the zip file to a share URI
        val shareUri = androidFileSystemShareOperatorGatewayImpl.resolveShareUriFromFile(zipFile)

        return shareUri

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
        val isShellPluginExportSuccessful = shizukuUserServiceGatewayImpl.findShizukuUserService()
            ?.exportShellPlugin(
                pluginManifestRoom.pluginPackageName,
                shellPluginExportZipFile.path
            ) ?: false

        if (!isShellPluginExportSuccessful) {
            throw IllegalStateException("Failed to export shell plugin. pluginPackageName=${pluginManifestRoom.pluginPackageName}")
        }

        // Check if Zip is really available (option)
//      TODO("Check Zip integrity")

        // Convert the zip file to a share URI
        val shareUri = androidFileSystemShareOperatorGatewayImpl.resolveShareUriFromFile(shellPluginExportZipFile)

        return shareUri

    }
}
