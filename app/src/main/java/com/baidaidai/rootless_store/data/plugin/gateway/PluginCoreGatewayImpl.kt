package com.baidaidai.rootless_store.data.plugin.gateway

import android.content.Context
import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.remote.datasource.DownloadPluginPackage
import com.baidaidai.rootless_store.domain.plugin.gateway.PluginCoreGateway
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.plugin.manifest.LocalManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import java.io.File
import javax.inject.Inject

class PluginCoreGatewayImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val downloadPluginPackage: DownloadPluginPackage,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
): PluginCoreGateway {
    private val defaultPluginLocation = File(context.getExternalFilesDir(null), "Plugin")
    private val defaultEnvironmentLocation = File(context.filesDir, "Environment")

    // Create
    override fun installPluginFromLocal(originFileURI: Uri) {
        _pre_intallPlugin(originFileURI)
    }
    override fun installEnvironmentFromLocal(originFileURI: Uri) {
        _pre_intallEnvironment(originFileURI)
    }

    override suspend fun installPluginFromMarket(pluginURI: String, pluginManifestRemote: PluginManifestRemote) {
        val remotePluginContent: ByteReadChannel = downloadPluginPackage.usePluginURI(pluginURI).bodyAsChannel()
        val pluginPackageName = pluginManifestRemote.pluginPackageName
        _pre_intallPlugin(
            originFileByteChannel = remotePluginContent,
            destinationFileName = pluginPackageName
        )
    }

    // Update
    fun setPluginEntryPointExecutable(pluginManifestRoom: PluginManifestRoom){
        androidFileSystemCapabilityGatewayImpl.setPluginEntryPointExecutable(pluginManifestRoom)
    }
    fun setEnvironmentEntryPointExecutable(environmentManifestRoom: EnvironmentManifestRoom){
        androidFileSystemCapabilityGatewayImpl.setEnvironmentEntryPointExecutable(environmentManifestRoom)
    }

    // Read
    fun getEnvironmentRuntimePATH(environmentManifest: EnvironmentManifest): String{
        val environmentPackageName = environmentManifest.environmentPackageName

        return "$defaultEnvironmentLocation/$environmentPackageName"
    }

    fun getEnvironmentLDPATH(environmentManifest: EnvironmentManifest): String{
        val environmentPackageName = environmentManifest.environmentPackageName

        return environmentManifest.ldLibraryPath.joinToString(":") { libraryPath ->
            "$defaultEnvironmentLocation/$environmentPackageName/$libraryPath"
        }
    }
    fun getEnvironmentConfig(environmentManifest: EnvironmentManifest): Map<String, String> {
        return environmentManifest.env
    }

    // Delete
    override fun uninstallPlugin(
        pluginPackageName: String  // Should use pluginManifest<Room/Local>
    ) {
        androidFileSystemCapabilityGatewayImpl.deleteDirectoryByPackageName(pluginPackageName)
    }

    internal fun parsePluginManifest(originFileURI: Uri): PluginManifestLocal {
        return androidFileSystemCapabilityGatewayImpl.readRawPluginManifest(uri = originFileURI).let {
            androidFileSystemCapabilityGatewayImpl.readManifestJsonContent(it)
        }
    }
    internal fun parseEnvironmentManifest(originFileURI: Uri): EnvironmentManifestLocal {
        return androidFileSystemCapabilityGatewayImpl.readRawEnvironmentManifest(uri = originFileURI).let {
            androidFileSystemCapabilityGatewayImpl.readEnvironmentManifestJsonContent(it)
        }
    }

    internal fun judgeManifest(originFileURI: Uri): LocalManifest {
        val pluginManifestJson = androidFileSystemCapabilityGatewayImpl.readRawPluginManifest(uri = originFileURI)
        if (pluginManifestJson.isNotBlank()) {
            return LocalManifest.PluginManifestLocal
        }

        val environmentManifestJson = androidFileSystemCapabilityGatewayImpl.readRawEnvironmentManifest(uri = originFileURI)
        if (environmentManifestJson.isNotBlank()) {
            return LocalManifest.EnvironmentManifestLocal
        }

        error("Neither PluginManifest.json nor EnvironmentManifest.json was found.")
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
    private fun _pre_intallEnvironment(originFileURI: Uri, destination: File = defaultEnvironmentLocation) {
        if (androidFileSystemCapabilityGatewayImpl.confirmEnvironmentPathExists()){
            androidFileSystemCapabilityGatewayImpl.unzipEnvironmentFromFile(
                originFileURI = originFileURI,
                pluginRootDirectory = destination
            )
        }else{
            androidFileSystemCapabilityGatewayImpl.createFileDir("Environment")
            _pre_intallEnvironment(originFileURI)
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