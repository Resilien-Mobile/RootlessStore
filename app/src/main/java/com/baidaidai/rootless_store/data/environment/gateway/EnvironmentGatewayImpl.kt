package com.baidaidai.rootless_store.data.environment.gateway

import android.content.Context
import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.remote.datasource.DownloadPluginPackage
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestLocal
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRemote
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import java.io.File
import javax.inject.Inject

class EnvironmentGatewayImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val downloadPluginPackage: DownloadPluginPackage,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
) {
    private val defaultEnvironmentLocation = File(context.filesDir, "Environment")

    // Install Environment
    fun installEnvironmentFromLocal(originFileURI: Uri) {
        installEnvironment(originFileURI)
    }

    suspend fun installEnvironmentFromMarket(
        environmentURI: String,
        environmentManifestRemote: EnvironmentManifestRemote
    ) {
        val remoteEnvironmentContent = downloadPluginPackage.usePluginURI(environmentURI).bodyAsChannel()
        val environmentPackageName = environmentManifestRemote.environmentPackageName
        installEnvironment(
            originFileByteChannel = remoteEnvironmentContent,
            destinationFileName = environmentPackageName
        )
    }

    // Uninstall Environment
    fun uninstallEnvironment(environmentPackageName: String) {
        androidFileSystemCapabilityGatewayImpl.deleteEnvironmentDirectoryByPackageName(environmentPackageName)
    }

    // Environment Operation
    fun setEnvironmentEntryPointExecutable(environmentManifest: EnvironmentManifest) {
        androidFileSystemCapabilityGatewayImpl.setEnvironmentEntryPointExecutable(environmentManifest)
    }

    // Environment Config Getter
    fun resolveEnvironmentRuntimePath(environmentManifest: EnvironmentManifest): String {
        val environmentPackageName = environmentManifest.environmentPackageName

        return "$defaultEnvironmentLocation/$environmentPackageName"
    }

    fun resolveEnvironmentLdPath(environmentManifest: EnvironmentManifest): String {
        val environmentPackageName = environmentManifest.environmentPackageName

        return environmentManifest.ldLibraryPath.joinToString(":") { libraryPath ->
            "$defaultEnvironmentLocation/$environmentPackageName/$libraryPath"
        }
    }

    fun resolveEnvironmentConfig(environmentManifest: EnvironmentManifest): Map<String, String> {
        return environmentManifest.env
    }


    // Get Raw EnvironmentManifestLocal
    fun getRawEnvironmentManifestLocal(originFileURI: Uri): EnvironmentManifestLocal {
        return androidFileSystemCapabilityGatewayImpl.readRawEnvironmentManifest(uri = originFileURI).let {
            androidFileSystemCapabilityGatewayImpl.readEnvironmentManifestJsonContent(it)
        }
    }

    // Only Un-Zip operation, which from given zip file
    private fun installEnvironment(
        originFileURI: Uri,
        destination: File = defaultEnvironmentLocation
    ) {
        if (androidFileSystemCapabilityGatewayImpl.confirmEnvironmentPathExists()) {
            androidFileSystemCapabilityGatewayImpl.unzipEnvironmentFromFile(
                originFileURI = originFileURI,
                pluginRootDirectory = destination
            )
        } else {
            androidFileSystemCapabilityGatewayImpl.createFileDir("Environment")
            installEnvironment(originFileURI)
        }
    }

    private fun installEnvironment(
        originFileByteChannel: ByteReadChannel,
        destination: File = defaultEnvironmentLocation,
        destinationFileName: String
    ) {
        if (androidFileSystemCapabilityGatewayImpl.confirmEnvironmentPathExists()) {
            androidFileSystemCapabilityGatewayImpl.unZipEnvironmentFromURI(
                originFileByteChannel = originFileByteChannel,
                pluginRootDirectory = destination,
                directoryName = destinationFileName
            )
        } else {
            androidFileSystemCapabilityGatewayImpl.createFileDir("Environment")
            installEnvironment(originFileByteChannel, destination, destinationFileName)
        }
    }
}
