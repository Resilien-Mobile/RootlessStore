package com.baidaidai.rootless_store.data.environment.gateway

import com.baidaidai.rootless_store.domain.environment.gateway.EnvironmentGateway
import android.content.Context
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemUnzipOperatorGatewayImpl
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
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl,
    private val androidFileSystemUnzipOperatorGatewayImpl: AndroidFileSystemUnzipOperatorGatewayImpl
): EnvironmentGateway {
    private val defaultEnvironmentDirectory = File(context.filesDir, "Environment")

    // Install Environment
    override fun installEnvironmentFromLocal(originFile: File): Result<Unit> = runCatching {
        val environmentManifest = parseEnvironmentManifest(originFile).getOrThrow()
        androidFileSystemUnzipOperatorGatewayImpl.unzipEnvironmentFromFile(
            originFile = originFile,
            pluginRootDirectory = defaultEnvironmentDirectory,
            directoryName = environmentManifest.environmentPackageName
        )
    }

    override suspend fun installEnvironmentFromMarket(
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
    override fun uninstallEnvironment(environmentPackageName: String) {
        androidFileSystemCapabilityGatewayImpl.deleteEnvironmentDirectoryByPackageName(environmentPackageName)
    }

    // Environment Operation
    override fun setEnvironmentEntryPointExecutable(environmentManifest: EnvironmentManifest): Result<Unit> = runCatching {
        androidFileSystemCapabilityGatewayImpl.setEnvironmentEntryPointExecutable(environmentManifest)
    }

    // Environment Config Getter
    override fun resolveEnvironmentRuntimePath(environmentManifest: EnvironmentManifest): String {
        val environmentPackageName = environmentManifest.environmentPackageName

        return "$defaultEnvironmentDirectory/$environmentPackageName"
    }

    override fun resolveEnvironmentLdPath(environmentManifest: EnvironmentManifest): String {
        val environmentPackageName = environmentManifest.environmentPackageName

        return environmentManifest.ldLibraryPath.joinToString(":") { libraryPath ->
            "$defaultEnvironmentDirectory/$environmentPackageName/$libraryPath"
        }
    }

    override fun resolveEnvironmentConfig(environmentManifest: EnvironmentManifest): Map<String, String> {
        return environmentManifest.env
    }


    // Parse local environment manifest
    override fun parseEnvironmentManifest(originFile: File): Result<EnvironmentManifest> = runCatching {
        androidFileSystemReadOperatorGatewayImpl.loadRawEnvironmentManifest(originFile).let {
            androidFileSystemReadOperatorGatewayImpl.parseEnvironmentManifest(it)
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
