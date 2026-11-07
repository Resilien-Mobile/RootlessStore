package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGatewayImpl
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemDefaultOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemDeleteOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemRezipOperatorGateway
import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class InstallPluginFromCodeBrickUseCase @Inject constructor(
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val androidFileSystemDefaultOperatorGateway: AndroidFileSystemDefaultOperatorGateway,
    private val androidFileSystemCreateOperatorGatewayImpl: AndroidFileSystemCreateOperatorGatewayImpl,
    private val androidFileSystemRezipOperatorGateway: AndroidFileSystemRezipOperatorGateway,
    private val androidFileSystemDeleteOperatorGateway: AndroidFileSystemDeleteOperatorGateway,
    private val pluginGatewayImpl: PluginGatewayImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl
) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend operator fun invoke(
        codeBrickConfig: CodeBrickConfig
    ) {

        // Build Plugin Manifest
        val pluginId = codeBrickConfig.unixTimestamp.toString()
        val pluginManifest = PluginManifest(
            installedVersion = "1.0.0",
            pluginRenderingName = codeBrickConfig.codeBrickTitle,
            pluginPackageName = codeBrickConfig.codeBrickTitle,
            pluginId = pluginId,
            iconUri = null,
            author = "CodeBrick",
            pluginDescription = "Generated from CodeBrick",
            requiredEnvironment = codeBrickConfig.codeBrickEnvironment,
            entryPoint = "index.sh",
            pluginRunModel = PluginRunModel.OneTime,
        )
        val pluginManifestJson = json.encodeToString(pluginManifest)

        if (pluginManifest.requiredEnvironment == ExecutionContext.ADB){
            installShellPluginFromCodeBrick(
                codeBrickConfig = codeBrickConfig,
                pluginManifest = pluginManifest,
                pluginManifestJson = pluginManifestJson
            )
            return
        }

        // Prepare Plugin Directory
        androidFileSystemCapabilityGatewayImpl.ensureFilesDirectory("Plugin")
        val pluginRootDirectory = File(androidFileSystemCapabilityGatewayImpl.getDefaultPluginDirectoryPath())
        val pluginPackageDirectory = androidFileSystemCapabilityGatewayImpl.resolveChildFile(
            parentDirectory = pluginRootDirectory,
            childName = codeBrickConfig.codeBrickTitle
        ).apply {
            mkdirs()
        }

        // Write Plugin Entry Point
        androidFileSystemCapabilityGatewayImpl.writeTextFile(
            parentDirectory = pluginPackageDirectory,
            fileName = "index.sh",
            content = codeBrickConfig.codeBrickContent
        )

        // Write Plugin Manifest
        androidFileSystemCapabilityGatewayImpl.writeTextFile(
            parentDirectory = pluginPackageDirectory,
            fileName = "PluginManifest.json",
            content = pluginManifestJson
        )

        // Set Plugin Entry Point Executable
        pluginGatewayImpl.setPluginEntryPointExecutable(pluginManifest)

        // Insert result to PluginRepository
        pluginRepositoryImpl.addPlugin(pluginManifest)
        pluginStatusRepositoryImpl.registerPluginStatus(pluginManifest.pluginId, PluginOrigin.Local)
    }

    private suspend fun installShellPluginFromCodeBrick(
        codeBrickConfig: CodeBrickConfig,
        pluginManifest: PluginManifest,
        pluginManifestJson: String
    ) {

        // Prepare Temporary Shell Plugin Package Directory
        val shellPluginStagingDirectory = androidFileSystemDefaultOperatorGateway.getExternalAppFilesDirectoryFile()
        val temporaryShellPluginPackageDirectory = File(
            shellPluginStagingDirectory,
            pluginManifest.pluginPackageName
        )
        androidFileSystemDeleteOperatorGateway.deleteFileOrDirectory(
            temporaryShellPluginPackageDirectory.path
        )
        temporaryShellPluginPackageDirectory.mkdirs()

        // Write Plugin Entry Point
        androidFileSystemCreateOperatorGatewayImpl.writeTextFile(
            parentDirectory = temporaryShellPluginPackageDirectory,
            fileName = pluginManifest.entryPoint,
            content = codeBrickConfig.codeBrickContent
        )

        // Write Plugin Manifest
        androidFileSystemCreateOperatorGatewayImpl.writeTextFile(
            parentDirectory = temporaryShellPluginPackageDirectory,
            fileName = "PluginManifest.json",
            content = pluginManifestJson
        )

        // Re-Zip Temporary Shell Plugin Package Directory to _template_.zip
        val shellPluginStagingFile = File(shellPluginStagingDirectory, "_template_.zip")
        androidFileSystemRezipOperatorGateway.rezipFromFile(
            originPluginFile = temporaryShellPluginPackageDirectory,
            targetZipFile = shellPluginStagingFile
        )

        // Shizuku File Flow
        val isShellPluginInstallSuccessful = shizukuUserServiceGatewayImpl.findShizukuUserService()
            ?.installShellPlugin(
                shellPluginStagingFile.path,
                pluginManifest.pluginPackageName,
                pluginManifest.entryPoint
            ) ?: false

        // Delete Temporary Shell Plugin Package Directory
        androidFileSystemDeleteOperatorGateway.deleteFileOrDirectory(
            temporaryShellPluginPackageDirectory.path
        )

        // Delete _template_.zip
        androidFileSystemDeleteOperatorGateway.deleteFileOrDirectory(
            shellPluginStagingFile.path
        )

        if (!isShellPluginInstallSuccessful) {
            return
        }

        // Insert result to PluginRepository
        pluginRepositoryImpl.addPlugin(pluginManifest)
        pluginStatusRepositoryImpl.registerPluginStatus(pluginManifest.pluginId, PluginOrigin.Local)
    }

}
