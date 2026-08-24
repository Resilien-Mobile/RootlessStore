package com.baidaidai.rootless_store.data.plugin.gateway

import android.content.Context
import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.market.remote.datasource.MarketPackageRemoteDataSource
import com.baidaidai.rootless_store.domain.plugin.gateway.PluginGateway
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
    private val marketPackageRemoteDataSource: MarketPackageRemoteDataSource,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
): PluginGateway {
    private val defaultPluginLocation = File(context.getExternalFilesDir(null), "Plugin")

    // Create
    override fun installPluginFromLocal(originFileUri: Uri) {
        installPluginPackage(originFileUri)
    }

    override suspend fun installPluginFromMarket(pluginUrl: String, pluginManifestRemote: PluginManifestRemote) {
        val remotePluginContent: ByteReadChannel = marketPackageRemoteDataSource.fetchPackage(pluginUrl).bodyAsChannel()
        val pluginPackageName = pluginManifestRemote.pluginPackageName
        installPluginPackage(
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

    private fun installPluginPackage(originFileUri: Uri, destination: File = defaultPluginLocation) {
        if (androidFileSystemCapabilityGatewayImpl.hasPluginDirectory()){
            androidFileSystemCapabilityGatewayImpl.unzipFromFile(
                originFileUri = originFileUri,
                pluginRootDirectory = destination
            )
        }else{
            androidFileSystemCapabilityGatewayImpl.ensureFilesDirectory("Plugin")
            installPluginPackage(originFileUri)
        }
    }

    private fun installPluginPackage(originFileByteChannel: ByteReadChannel, destination: File = defaultPluginLocation, destinationFileName: String) {
        if (androidFileSystemCapabilityGatewayImpl.hasPluginDirectory()){
            androidFileSystemCapabilityGatewayImpl.unzipFromUri(
                originFileByteChannel = originFileByteChannel,
                pluginRootDirectory = destination,
                directoryName = destinationFileName
            )
        }else{
            androidFileSystemCapabilityGatewayImpl.ensureFilesDirectory("Plugin")
            installPluginPackage(originFileByteChannel,destination,destinationFileName)
        }
    }
}
