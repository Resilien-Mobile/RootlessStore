package com.baidaidai.rootless_store.application.plugin

import android.net.Uri
import com.baidaidai.illusioncube.IllusionCube
import com.baidaidai.rootless_store.core.util.OutOfStringLike
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDeleteOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReZipOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemUnZipOperatorGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.manifest.MagiskProp
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class InstallMagiskPluginUseCase @Inject constructor(
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl,
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl,
    private val androidFileSystemCreateOperatorGatewayImpl: AndroidFileSystemCreateOperatorGatewayImpl,
    private val androidFileSystemUnZipOperatorGatewayImpl: AndroidFileSystemUnZipOperatorGatewayImpl,
    private val androidFileSystemReZipOperatorGatewayImpl: AndroidFileSystemReZipOperatorGatewayImpl,
    private val androidFileSystemDeleteOperatorGatewayImpl: AndroidFileSystemDeleteOperatorGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl
) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend operator fun invoke(uri: Uri): PluginError? {
        return try {

            // Read module.prop
            val magiskModulePropContent = androidFileSystemReadOperatorGatewayImpl.readRawMagiskModuleProp(uri)
            if (magiskModulePropContent.isBlank()) {
                return PluginError(
                    errorMessage = "Magisk module.prop was not found",
                    errorCause = "Unsupported magisk module package: $uri"
                )
            }

            // Judge module.prop by IllusionCube
            val magiskModulePropAvailable = IllusionCube.Prop.judgeIfProp(magiskModulePropContent)
            if (!magiskModulePropAvailable) {
                return PluginError(
                    errorMessage = "Magisk module.prop is invalid",
                    errorCause = "IllusionCube cannot recognize this module.prop as prop format."
                )
            }

            // Convert module.prop to ModuleProp
            val magiskModulePropJson = IllusionCube.Prop(magiskModulePropContent).convertPropToJson()
            val magiskProp = json.decodeFromString<MagiskProp>(magiskModulePropJson)

            // Detect AXManager style action script.
            // Some Magisk modules use action.sh as the actual entry point instead of service.sh.
            val actionScriptExists = androidFileSystemReadOperatorGatewayImpl.confirmFileExistsInZip(
                uri = uri,
                fileName = "action.sh"
            )
            val magiskModuleEntryPoint = if (actionScriptExists) {
                "action.sh"
            } else {
                "service.sh"
            }

            // Mapper ModuleProp to PluginManifestLocal
            val pluginManifestLocal = magiskProp.toPluginManifestLocal(
                entryPoint = magiskModuleEntryPoint
            )
            val pluginManifestJson = json.encodeToString(pluginManifestLocal)

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

            androidFileSystemUnZipOperatorGatewayImpl.unzipFromFileToDirectory(
                originFileURI = uri,
                targetDirectory = magiskTemplateDirectory
            )

            androidFileSystemCreateOperatorGatewayImpl.writeTextFile(
                parentDirectory = magiskTemplateDirectory,
                fileName = "PluginManifest.json",
                content = pluginManifestJson
            )

            androidFileSystemReZipOperatorGatewayImpl.rezipFromFile(
                originPluginFile = magiskTemplateDirectory,
                targetZipFile = magiskTemplateZipFile
            )

            val deleteMagiskTemplateDirectoryResult = androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(
                magiskTemplateDirectory.path
            )
            if (!deleteMagiskTemplateDirectoryResult) {
                return PluginError(
                    errorMessage = "Delete magisk template directory failed",
                    errorCause = "Failed to delete ${magiskTemplateDirectory.path}"
                )
            }

            // Shizuku File Flow
            val shellPluginInstallResult = shizukuUserServiceGatewayImpl.getShizukuUserService()
                ?.installShellPlugin(
                    magiskTemplateZipFile.path,
                    pluginManifestLocal.pluginPackageName,
                    pluginManifestLocal.entryPoint
                ) ?: false

            val deleteMagiskTemplateZipFileResult = androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(
                magiskTemplateZipFile.path
            )

            if (!shellPluginInstallResult) {
                return PluginError(
                    errorMessage = "Install magisk shell plugin failed",
                    errorCause = "Failed to copy magisk shell plugin into com.android.shell private directory. pluginPackageName=${pluginManifestLocal.pluginPackageName}, entryPoint=${pluginManifestLocal.entryPoint}"
                )
            }

            if (!deleteMagiskTemplateZipFileResult) {
                return PluginError(
                    errorMessage = "Delete magisk template zip failed",
                    errorCause = "Failed to delete ${magiskTemplateZipFile.path}"
                )
            }

            // Insert result to PluginRepositoryImpl
            pluginRepositoryImpl.insertPluginInfo(pluginManifestLocal)

            null
        } catch (error: Throwable) {
            PluginError(
                errorMessage = error.message ?: "Install magisk plugin crashed",
                errorCause = error.stackTrace.OutOfStringLike()
            )
        }
    }

    private fun MagiskProp.toPluginManifestLocal(
        entryPoint: String
    ): PluginManifestLocal {
        return PluginManifestLocal(
            installedVersion = version,
            pluginRenderingName = name,
            pluginPackageName = name.replace(" ",""), // Avoid spacing, Prevent misidentification
            pluginID = id,
            iconURI = null,
            author = author,
            pluginDescription = description,
            requiredEnvironment = HosterOverallStatus.ADB,
            entryPoint = entryPoint,
            pluginRunModel = PluginRunModel.Daemon,
            executableFiles = null
        )
    }
}
