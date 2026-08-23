package com.baidaidai.rootless_store.data.plugin.gateway

import android.content.Context
import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.remote.datasource.DownloadPluginPackage
import com.baidaidai.rootless_store.domain.plugin.gateway.PluginCoreGateway
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import java.io.File
import javax.inject.Inject

class PluginGatewayImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val downloadPluginPackage: DownloadPluginPackage,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
): PluginCoreGateway {
    private val defaultPluginLocation = File(context.getExternalFilesDir(null), "Plugin")

    // Create
    override fun installPluginFromLocal(originFileUri: Uri) {
        _pre_intallPlugin(originFileUri)
    }

    override suspend fun installPluginFromMarket(pluginUri: String, pluginManifestRemote: PluginManifestRemote) {
        val remotePluginContent: ByteReadChannel = downloadPluginPackage.usePluginUri(pluginUri).bodyAsChannel()
        val pluginPackageName = pluginManifestRemote.pluginPackageName
        _pre_intallPlugin(
            originFileByteChannel = remotePluginContent,
            destinationFileName = pluginPackageName
        )
    }

    // Operator
    fun setPluginEntryPointExecutable(pluginManifest: PluginManifest){
        androidFileSystemCapabilityGatewayImpl.setPluginEntryPointExecutable(pluginManifest)
    }

    // Delete
    override fun uninstallPlugin(
        pluginPackageName: String  // Should use pluginManifest<Room/Local>
    ) {
        androidFileSystemCapabilityGatewayImpl.deleteDirectoryByPackageName(pluginPackageName)
    }

    fun parsePluginManifest(originFileUri: Uri): PluginManifestLocal {
        return androidFileSystemCapabilityGatewayImpl.loadRawPluginManifest(uri = originFileUri).let {
            androidFileSystemCapabilityGatewayImpl.parsePluginManifest(it)
        }
    }

    private fun _pre_intallPlugin(originFileUri: Uri, destination: File = defaultPluginLocation) {
        if (androidFileSystemCapabilityGatewayImpl.confirmPluginPathExists()){
            androidFileSystemCapabilityGatewayImpl.unzipFromFile(
                originFileUri = originFileUri,
                pluginRootDirectory = destination
            )
        }else{
            androidFileSystemCapabilityGatewayImpl.createFileDir("Plugin")
            _pre_intallPlugin(originFileUri)
        }
    }

    private fun _pre_intallPlugin(originFileByteChannel: ByteReadChannel, destination: File = defaultPluginLocation, destinationFileName: String) {
        if (androidFileSystemCapabilityGatewayImpl.confirmPluginPathExists()){
            androidFileSystemCapabilityGatewayImpl.unzipFromUri(
                originFileByteChannel = originFileByteChannel,
                pluginRootDirectory = destination,
                directoryName = destinationFileName
            )
        }else{
            androidFileSystemCapabilityGatewayImpl.createFileDir("Plugin")
            _pre_intallPlugin(originFileByteChannel,destination,destinationFileName)
        }
    }
}
