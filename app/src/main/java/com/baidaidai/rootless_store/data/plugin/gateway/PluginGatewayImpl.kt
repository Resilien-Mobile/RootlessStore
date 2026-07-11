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
    override fun installPluginFromLocal(originFileURI: Uri) {
        _pre_intallPlugin(originFileURI)
    }

    override suspend fun installPluginFromMarket(pluginURI: String, pluginManifestRemote: PluginManifestRemote) {
        val remotePluginContent: ByteReadChannel = downloadPluginPackage.usePluginURI(pluginURI).bodyAsChannel()
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

    fun parsePluginManifest(originFileURI: Uri): PluginManifestLocal {
        return androidFileSystemCapabilityGatewayImpl.readRawPluginManifest(uri = originFileURI).let {
            androidFileSystemCapabilityGatewayImpl.readManifestJsonContent(it)
        }
    }

    private fun _pre_intallPlugin(originFileURI: Uri, destination: File = defaultPluginLocation) {
        if (androidFileSystemCapabilityGatewayImpl.confirmPluginPathExists()){
            androidFileSystemCapabilityGatewayImpl.unzipFromFile(
                originFileURI = originFileURI,
                pluginRootDirectory = destination
            )
        }else{
            androidFileSystemCapabilityGatewayImpl.createFileDir("Plugin")
            _pre_intallPlugin(originFileURI)
        }
    }

    private fun _pre_intallPlugin(originFileByteChannel: ByteReadChannel, destination: File = defaultPluginLocation, destinationFileName: String) {
        if (androidFileSystemCapabilityGatewayImpl.confirmPluginPathExists()){
            androidFileSystemCapabilityGatewayImpl.unZipFromURI(
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