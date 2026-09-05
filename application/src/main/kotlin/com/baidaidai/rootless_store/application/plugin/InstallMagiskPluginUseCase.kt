package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.illusioncube.IllusionCube
import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDeleteOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemRezipOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemUnzipOperatorGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.manifest.MagiskProp
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class InstallMagiskPluginUseCase @Inject constructor(
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl,
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl,
    private val androidFileSystemCreateOperatorGatewayImpl: AndroidFileSystemCreateOperatorGatewayImpl,
    private val androidFileSystemUnzipOperatorGatewayImpl: AndroidFileSystemUnzipOperatorGatewayImpl,
    private val androidFileSystemRezipOperatorGatewayImpl: AndroidFileSystemRezipOperatorGatewayImpl,
    private val androidFileSystemDeleteOperatorGatewayImpl: AndroidFileSystemDeleteOperatorGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl
) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend operator fun invoke(uri: Uri): PluginError? {
        return try {

            // Read module.prop
            val magiskModulePropContent = androidFileSystemReadOperatorGatewayImpl.loadRawMagiskModuleProp(uri)
            if (magiskModulePropContent.isBlank()) {
                return PluginError(
                    errorMessage = "Magisk module.prop was not found",
                    errorCause = "Unsupported magisk module package: $uri"
                )
            }

            // Judge module.prop by IllusionCube
            val isMagiskModulePropValid = IllusionCube.Prop.validate(magiskModulePropContent)
            if (!isMagiskModulePropValid) {
                return PluginError(
                    errorMessage = "Magisk module.prop is invalid",
                    errorCause = "IllusionCube cannot recognize this module.prop as prop format."
                )
            }

            // Convert module.prop to ModuleProp
            val magiskModulePropJson = IllusionCube.Prop(magiskModulePropContent).encodeAsJson()
            val magiskProp = json.decodeFromString<MagiskProp>(magiskModulePropJson)

            // Detect AXManager style action script.
            // Some Magisk modules use action.sh as the actual entry point instead of service.sh.
            val hasActionScript = androidFileSystemReadOperatorGatewayImpl.hasFileInZip(
                uri = uri,
                fileName = "action.sh"
            )
            val magiskModuleEntryPoint = if (hasActionScript) {
                "action.sh"
            } else {
                "service.sh"
            }

            // Mapper ModuleProp to PluginManifest
            val pluginManifest = magiskProp.toPluginManifest(
                entryPoint = magiskModuleEntryPoint
            )
            val pluginManifestJson = json.encodeToString(pluginManifest)

            // Build PluginManifest.json into temporary zip/plugin package
            val magiskStagingDirectory = androidFileSystemDefaultOperatorGatewayImpl.getExternalAppMagiskDirectoryFile()
            val magiskTemplateDirectory = androidFileSystemDefaultOperatorGatewayImpl.getExternalAppMagiskTemplateDirectoryFile()
            val magiskTemplateZipFile = File(magiskStagingDirectory, "_template_.zip")

            androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(
                magiskTemplateDirectory.path
            )  // Delete Template Directory, Avoid old content
            androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(
                magiskTemplateZipFile.path
            ) // Delete _template_.zip, Avoid old content
            magiskTemplateDirectory.mkdirs()

            androidFileSystemUnzipOperatorGatewayImpl.unzipFromFileToDirectory(
                originFileUri = uri,
                targetDirectory = magiskTemplateDirectory
            )

            androidFileSystemCreateOperatorGatewayImpl.writeTextFile(
                parentDirectory = magiskTemplateDirectory,
                fileName = "PluginManifest.json",
                content = pluginManifestJson
            )

            androidFileSystemRezipOperatorGatewayImpl.rezipFromFile(
                originPluginFile = magiskTemplateDirectory,
                targetZipFile = magiskTemplateZipFile
            )

            val isMagiskTemplateDirectoryDeleted = androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(
                magiskTemplateDirectory.path
            )
            if (!isMagiskTemplateDirectoryDeleted) {
                return PluginError(
                    errorMessage = "Delete magisk template directory failed",
                    errorCause = "Failed to delete ${magiskTemplateDirectory.path}"
                )
            }

            // Shizuku File Flow
            val isShellPluginInstallSuccessful = shizukuUserServiceGatewayImpl.findShizukuUserService()
                ?.installShellPlugin(
                    magiskTemplateZipFile.path,
                    pluginManifest.pluginPackageName,
                    pluginManifest.entryPoint
                ) ?: false

            val isMagiskTemplateArchiveDeleted = androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(
                magiskTemplateZipFile.path
            )

            if (!isShellPluginInstallSuccessful) {
                return PluginError(
                    errorMessage = "Install magisk shell plugin failed",
                    errorCause = "Failed to copy magisk shell plugin into com.android.shell private directory. pluginPackageName=${pluginManifest.pluginPackageName}, entryPoint=${pluginManifest.entryPoint}"
                )
            }

            if (!isMagiskTemplateArchiveDeleted) {
                return PluginError(
                    errorMessage = "Delete magisk template zip failed",
                    errorCause = "Failed to delete ${magiskTemplateZipFile.path}"
                )
            }

            // Insert result to PluginRepository
            pluginRepositoryImpl.addPlugin(pluginManifest)
            pluginStatusRepositoryImpl.registerPluginStatus(pluginManifest.pluginId, PluginOrigin.Local)

            null
        } catch (error: Throwable) {
            PluginError(
                errorMessage = error.message ?: "Install magisk plugin crashed",
                errorCause = error.stackTrace.formatAsMultilineString()
            )
        }
    }

    private fun MagiskProp.toPluginManifest(
        entryPoint: String
    ): PluginManifest {
        return PluginManifest(
            installedVersion = version,
            pluginRenderingName = name,
            pluginPackageName = name.replace(" ",""), // Avoid spacing, Prevent misidentification
            pluginId = id,
            iconUri = null,
            author = author,
            pluginDescription = description,
            requiredEnvironment = ExecutionContext.ADB,
            entryPoint = entryPoint,
            pluginRunModel = PluginRunModel.Daemon
        )
    }
}
