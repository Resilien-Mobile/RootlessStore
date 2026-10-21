package com.baidaidai.rootless_store.data.plugin.gateway

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemChmodOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDeleteOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemUnzipOperatorGatewayImpl
import com.baidaidai.rootless_store.data.market.remote.datasource.MarketPackageRemoteDataSource
import com.baidaidai.rootless_store.domain.plugin.gateway.PluginGateway
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import java.io.File
import javax.inject.Inject

class PluginGatewayImpl @Inject constructor(
    private val marketPackageRemoteDataSource: MarketPackageRemoteDataSource,
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl,
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl,
    private val androidFileSystemCreateOperatorGatewayImpl: AndroidFileSystemCreateOperatorGatewayImpl,
    private val androidFileSystemUnzipOperatorGatewayImpl: AndroidFileSystemUnzipOperatorGatewayImpl,
    private val androidFileSystemDeleteOperatorGatewayImpl: AndroidFileSystemDeleteOperatorGatewayImpl,
    private val androidFileSystemChmodOperatorGatewayImpl: AndroidFileSystemChmodOperatorGatewayImpl
): PluginGateway {

    // Create
    fun installPluginFromLocal(originFile: File): Result<Unit> = runCatching {
        val pluginManifest = parsePluginManifest(originFile).getOrThrow()
        val targetDirectory = resolvePluginPackageDirectory(pluginManifest.pluginPackageName)

        androidFileSystemUnzipOperatorGatewayImpl.unzipFromFileToDirectory(
            originFile = originFile,
            targetDirectory = targetDirectory
        )
    }

    override suspend fun installPluginFromMarket(pluginUrl: String, pluginManifest: PluginManifest) {
        val remotePluginContent: ByteReadChannel = marketPackageRemoteDataSource.fetchPackage(pluginUrl).bodyAsChannel()
        val targetDirectory = resolvePluginPackageDirectory(pluginManifest.pluginPackageName)

        androidFileSystemUnzipOperatorGatewayImpl.unzipFromByteChannelToDirectory(
            originFileByteChannel = remotePluginContent,
            targetDirectory = targetDirectory
        )
    }

    // Operator
    fun setPluginEntryPointExecutable(pluginManifest: PluginManifest): Result<Unit> = runCatching {
        check(
            androidFileSystemChmodOperatorGatewayImpl.setPluginEntryPointExecutable(pluginManifest)
        ) {
            "Could not make plugin entry point executable"
        }
    }

    // Delete
    override fun uninstallPlugin(
        pluginPackageName: String  // Should use pluginManifest<Room/Local>
    ): Result<Unit> = runCatching {
        check(
            androidFileSystemDeleteOperatorGatewayImpl.deleteDirectoryByPackageName(pluginPackageName)
        ) {
            "Could not delete plugin directory"
        }
    }

    fun parsePluginManifest(originFile: File): Result<PluginManifest> = runCatching {
        androidFileSystemReadOperatorGatewayImpl.loadRawPluginManifest(originFile).let {
            androidFileSystemReadOperatorGatewayImpl.parsePluginManifest(it)
        }
    }

    private fun resolvePluginPackageDirectory(pluginPackageName: String): File {
        return androidFileSystemCreateOperatorGatewayImpl.resolveChildFile(
            parentDirectory = androidFileSystemDefaultOperatorGatewayImpl.getInternalPluginDirectoryFile(),
            childName = pluginPackageName
        )
    }
}
