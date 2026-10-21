package com.baidaidai.rootless_store.application.install

import com.baidaidai.rootless_store.application.environment.InstallEnvironmentUseCase
import com.baidaidai.rootless_store.application.plugin.InstallMagiskPluginUseCase
import com.baidaidai.rootless_store.application.plugin.InstallPluginUseCase
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.install.model.LocalPackageType
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import java.io.File
import javax.inject.Inject

class InstallLocalPackageUseCase @Inject constructor(
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl,
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl,
    private val installPluginUseCase: InstallPluginUseCase,
    private val installEnvironmentUseCase: InstallEnvironmentUseCase,
    private val installMagiskPluginUseCase: InstallMagiskPluginUseCase
) {
    suspend operator fun invoke(): Result<Unit, PluginError> {

        val packageDirectory = androidFileSystemDefaultOperatorGatewayImpl
            .getExternalAppPackageCacheDirectoryFile()
        val packageFile = File(packageDirectory, "package.zip")

        /**
         * 关于为什么这里反而需要文件指针，对于此我是这样想的
         *
         * 虽然，不使用文件指针，可以让结构更清晰，但是相对的，重复样板代码会散落到
         * 各个use case中，导致代码冗长；而直接提供指针，则恢复逻辑不会散落到use case中
         * 虽然多些耦合，但是代码看着不烧心
         */
        return when (resolveLocalPackageType(packageFile)) {
            LocalPackageType.Plugin -> installPluginUseCase(packageFile)
            LocalPackageType.Environment -> installEnvironmentUseCase(packageFile)
            LocalPackageType.MagiskModule -> installMagiskPluginUseCase(packageFile)
            null -> {
                Err(
                    PluginError(
                        errorMessage = "Neither PluginManifest.json nor EnvironmentManifest.json was found.",
                        errorCause = "Unsupported local package: ${packageFile.path}"
                    )
                )
            }
        }
    }

    private fun resolveLocalPackageType(packageFile: File): LocalPackageType? {
        val pluginManifestJson = androidFileSystemReadOperatorGatewayImpl.loadRawPluginManifest(packageFile)
        if (pluginManifestJson.isNotBlank()) {
            return LocalPackageType.Plugin
        }

        val environmentManifestJson = androidFileSystemReadOperatorGatewayImpl.loadRawEnvironmentManifest(packageFile)
        if (environmentManifestJson.isNotBlank()) {
            return LocalPackageType.Environment
        }

        val magiskModuleProp = androidFileSystemReadOperatorGatewayImpl.loadRawMagiskModuleProp(packageFile)
        if (magiskModuleProp.isNotBlank()) {
            return LocalPackageType.MagiskModule
        }

        return null
    }
}
