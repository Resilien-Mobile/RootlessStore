package com.baidaidai.rootless_store.data.environment.gateway

import android.content.Context
import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.market.remote.datasource.MarketPackageRemoteDataSource
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import java.io.File
import javax.inject.Inject

class EnvironmentGatewayImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val marketPackageRemoteDataSource: MarketPackageRemoteDataSource,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
) {
    private val defaultEnvironmentDirectory = File(context.filesDir, "Environment")

    // Install Environment
    fun installEnvironmentFromLocal(originFileUri: Uri): Result<Unit> = runCatching {
        installEnvironment(originFileUri)
    }

    suspend fun installEnvironmentFromMarket(
        environmentUrl: String,
        environmentManifest: EnvironmentManifest
    ) {
        val remoteEnvironmentContent = marketPackageRemoteDataSource.fetchPackage(environmentUrl).bodyAsChannel()
        val environmentPackageName = environmentManifest.environmentPackageName
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
    fun setEnvironmentEntryPointExecutable(environmentManifest: EnvironmentManifest): Result<Unit> = runCatching {
        androidFileSystemCapabilityGatewayImpl.setEnvironmentEntryPointExecutable(environmentManifest)
    }

    // Environment Config Getter
    fun resolveEnvironmentRuntimePath(environmentManifest: EnvironmentManifest): String {
        val environmentPackageName = environmentManifest.environmentPackageName

        return "$defaultEnvironmentDirectory/$environmentPackageName"
    }

    fun resolveEnvironmentLdPath(environmentManifest: EnvironmentManifest): String {
        val environmentPackageName = environmentManifest.environmentPackageName

        return environmentManifest.ldLibraryPath.joinToString(":") { libraryPath ->
            "$defaultEnvironmentDirectory/$environmentPackageName/$libraryPath"
        }
    }

    fun resolveEnvironmentConfig(environmentManifest: EnvironmentManifest): Map<String, String> {
        return environmentManifest.env
    }


    // Parse local environment manifest
    fun parseEnvironmentManifest(originFileUri: Uri): Result<EnvironmentManifest> = runCatching {
        androidFileSystemCapabilityGatewayImpl.loadRawEnvironmentManifest(uri = originFileUri).let {
            androidFileSystemCapabilityGatewayImpl.parseEnvironmentManifest(it)
        }
    }

    // Only Un-Zip operation, which from given zip file
    private fun installEnvironment(
        originFileUri: Uri,
        destinationDirectory: File = defaultEnvironmentDirectory
    ) {
        if (androidFileSystemCapabilityGatewayImpl.hasEnvironmentDirectory()) {
            androidFileSystemCapabilityGatewayImpl.unzipEnvironmentFromFile(
                originFileUri = originFileUri,
                pluginRootDirectory = destinationDirectory
            )
        } else {
            androidFileSystemCapabilityGatewayImpl.ensureFilesDirectory("Environment")
            installEnvironment(originFileUri)
        }
    }

    private fun installEnvironment(
        originFileByteChannel: ByteReadChannel,
        destinationDirectory: File = defaultEnvironmentDirectory,
        destinationFileName: String
    ) {
        if (androidFileSystemCapabilityGatewayImpl.hasEnvironmentDirectory()) {
            androidFileSystemCapabilityGatewayImpl.unzipEnvironmentFromUri(
                originFileByteChannel = originFileByteChannel,
                pluginRootDirectory = destinationDirectory,
                directoryName = destinationFileName
            )
        } else {
            androidFileSystemCapabilityGatewayImpl.ensureFilesDirectory("Environment")
            installEnvironment(originFileByteChannel, destinationDirectory, destinationFileName)
        }
    }
}
