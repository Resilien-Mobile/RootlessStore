package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDeleteOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReZipOperatorGatewayImpl
import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class ConvertCodeBrickToPluginUseCase @Inject constructor(
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl,
    private val androidFileSystemCreateOperatorGatewayImpl: AndroidFileSystemCreateOperatorGatewayImpl,
    private val androidFileSystemReZipOperatorGatewayImpl: AndroidFileSystemReZipOperatorGatewayImpl,
    private val androidFileSystemDeleteOperatorGatewayImpl: AndroidFileSystemDeleteOperatorGatewayImpl,
    private val pluginGatewayImpl: PluginGatewayImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl
) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend operator fun invoke(
        codeBrickConfig: CodeBrickConfig
    ) {

        // Build Plugin Manifest
        val pluginId = codeBrickConfig.unixTimeStamp.toString()
        val pluginManifestLocal = PluginManifestLocal(
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
        val pluginManifestJson = json.encodeToString(pluginManifestLocal)

        if (pluginManifestLocal.requiredEnvironment == HosterOverallStatus.ADB){
            convertShellCodeBrick(
                codeBrickConfig = codeBrickConfig,
                pluginManifestLocal = pluginManifestLocal,
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
        pluginGatewayImpl.setPluginEntryPointExecutable(pluginManifestLocal)

        // Insert result to PluginRepositoryImpl
        pluginRepositoryImpl.insertPlugin(pluginManifestLocal)
    }

    private suspend fun convertShellCodeBrick(
        codeBrickConfig: CodeBrickConfig,
        pluginManifestLocal: PluginManifestLocal,
        pluginManifestJson: String
    ) {

        // Prepare Temporary Shell Plugin Package Directory
        val shellPluginStagingDirectory = androidFileSystemDefaultOperatorGatewayImpl.getExternalAppDirectoryFile()
        val temporaryShellPluginPackageDirectory = File(
            shellPluginStagingDirectory,
            pluginManifestLocal.pluginPackageName
        )
        androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(
            temporaryShellPluginPackageDirectory.path
        )
        temporaryShellPluginPackageDirectory.mkdirs()

        // Write Plugin Entry Point
        androidFileSystemCreateOperatorGatewayImpl.writeTextFile(
            parentDirectory = temporaryShellPluginPackageDirectory,
            fileName = pluginManifestLocal.entryPoint,
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
        androidFileSystemReZipOperatorGatewayImpl.rezipFromFile(
            originPluginFile = temporaryShellPluginPackageDirectory,
            targetZipFile = shellPluginStagingFile
        )

        // Shizuku File Flow
        val shellPluginInstallResult = shizukuUserServiceGatewayImpl.findShizukuUserService()
            ?.installShellPlugin(
                shellPluginStagingFile.path,
                pluginManifestLocal.pluginPackageName,
                pluginManifestLocal.entryPoint
            ) ?: false

        // Delete Temporary Shell Plugin Package Directory
        androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(
            temporaryShellPluginPackageDirectory.path
        )

        // Delete _template_.zip
        androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(
            shellPluginStagingFile.path
        )

        if (!shellPluginInstallResult) {
            return
        }

        // Insert result to PluginRepositoryImpl
        pluginRepositoryImpl.insertPlugin(pluginManifestLocal)

    }

}
