package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import java.io.File
import javax.inject.Inject

class GetPluginShareLinkUseCase @Inject constructor(
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
) {
    operator fun invoke(
        pluginManifestRoom: PluginManifestRoom
    ): Uri {

        val defaultPluginCacheDirectory = androidFileSystemCapabilityGatewayImpl.getCachePluginDirectoryFile()

        // Get plugin package directory -> /file/Plugin/PLUGIN_DIRECTORY
        val pluginPackageDirectory = androidFileSystemCapabilityGatewayImpl.getPluginPackageDirectory(pluginManifestRoom)

        // Detect if cache/plugin available -> /cache/Plugin
        val pluginCacheDirectoryAvailable = androidFileSystemCapabilityGatewayImpl.confirmPluginCacheExists()

        if (!pluginCacheDirectoryAvailable){
            androidFileSystemCapabilityGatewayImpl.createCacheDir("Plugin")
        }

        // Zip to cache/plugin
        // Create void zip content -> /cache/Plugin/PLUGIN.zip
        val zipFile = androidFileSystemCapabilityGatewayImpl.createVoidFileDirectory(defaultPluginCacheDirectory,"${pluginManifestRoom.pluginPackageName}.zip")

        // Write Zip Byte
        androidFileSystemCapabilityGatewayImpl.rezipFromFile(
            originPluginFile = File(pluginPackageDirectory),
            targetZipFile = zipFile
        )

        // Check if Zip is really available (option)
//      TODO("Check Zip integrity")

        // Convert from zip to ShareLink
        val shareLink = androidFileSystemCapabilityGatewayImpl.getShareUriFromFile(zipFile)

        return shareLink

    }
}
