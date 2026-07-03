package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.gateway.PluginGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class ConvertOneCodeBrickToPluginUseCase @Inject constructor(
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val pluginGatewayImpl: PluginGatewayImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl
) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend operator fun invoke(
        codeBrickConfig: CodeBrickConfig
    ) {

        // Build Plugin Manifest
        val pluginID = codeBrickConfig.unixTimeStamp.toString()
        val pluginManifestLocal = PluginManifestLocal(
            installedVersion = "1.0.0",
            pluginRenderingName = codeBrickConfig.codeBrickTitle,
            pluginPackageName = codeBrickConfig.codeBrickTitle,
            pluginID = pluginID,
            iconURI = null,
            author = "CodeBrick",
            pluginDescription = "Generated from CodeBrick",
            requiredEnvironment = codeBrickConfig.codeBrickEnvironment,
            entryPoint = "index.sh"
        )
        val pluginManifestJson = json.encodeToString(pluginManifestLocal)

        // Prepare Plugin Directory
        androidFileSystemCapabilityGatewayImpl.createFileDir("Plugin")
        val pluginRootDirectory = File(androidFileSystemCapabilityGatewayImpl.getDefaultPluginDirectoryPath())
        val pluginPackageDirectory = androidFileSystemCapabilityGatewayImpl.createVoidFileDirectory(
            pluginRootDirectory = pluginRootDirectory,
            directoryName = codeBrickConfig.codeBrickTitle
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
        pluginRepositoryImpl.insertOnePluginInfo(pluginManifestLocal)
    }

}