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

        // Read module.prop
        val magiskModulePropContent = runCatching {
            androidFileSystemReadOperatorGatewayImpl.loadRawMagiskModuleProp(uri)
        }.getOrElse { throwable ->
            return PluginError(
                errorMessage = "Can't read magisk module.prop",
                errorCause = throwable.stackTrace.formatAsMultilineString(),
                errorCompanion = "Rootless Store could not read module.prop from this package. The zip may not be a Magisk module, or the file is not at the expected location."
            )
        }
        if (magiskModulePropContent.isBlank()) {
            return PluginError(
                errorMessage = "Magisk module.prop was not found",
                errorCause = "",
                errorCompanion = "This package does not contain module.prop, so it cannot be converted as a Magisk module."
            )
        }

        // Judge module.prop by IllusionCube
        val isMagiskModulePropValid = IllusionCube.Prop.validate(magiskModulePropContent)
        if (!isMagiskModulePropValid) {
            return PluginError(
                errorMessage = "Magisk module.prop is invalid",
                errorCause = "",
                errorCompanion = "module.prop exists, but its key-value content could not be recognized as a valid prop file."
            )
        }

        // Convert module.prop to MagiskProp
        val magiskProp = runCatching {
            val magiskModulePropJson = IllusionCube.Prop(magiskModulePropContent).encodeAsJson()
            json.decodeFromString<MagiskProp>(magiskModulePropJson)
        }.getOrElse { throwable ->
            return PluginError(
                errorMessage = "Can't parse magisk module.prop",
                errorCause = throwable.stackTrace.formatAsMultilineString(),
                errorCompanion = "module.prop was converted to JSON, but the result does not match the required MagiskProp fields."
            )
        }

        // Detect AXManager style action script.
        // Some Magisk modules use action.sh as the actual entry point instead of service.sh.
        val hasActionScript = runCatching {
            androidFileSystemReadOperatorGatewayImpl.hasFileInZip(
                uri = uri,
                fileName = "action.sh"
            )
        }.getOrElse { throwable ->
            return PluginError(
                errorMessage = "Can't detect magisk action script",
                errorCause = throwable.stackTrace.formatAsMultilineString(),
                errorCompanion = "Rootless Store could not inspect whether this module uses action.sh as its entry point."
            )
        }
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

        // Un-zip magisk module to template directory
        runCatching {
            androidFileSystemUnzipOperatorGatewayImpl.unzipFromFileToDirectory(
                originFileUri = uri,
                targetDirectory = magiskTemplateDirectory
            )
        }.onFailure { throwable ->
            return PluginError(
                errorMessage = "Can't unzip magisk module",
                errorCause = throwable.stackTrace.formatAsMultilineString(),
                errorCompanion = "The Magisk module was recognized, but Rootless Store could not extract it into the temporary conversion directory."
            )
        }

        // Write PluginManifest.json
        runCatching {
            androidFileSystemCreateOperatorGatewayImpl.writeTextFile(
                parentDirectory = magiskTemplateDirectory,
                fileName = "PluginManifest.json",
                content = pluginManifestJson
            )
        }.onFailure { throwable ->
            return PluginError(
                errorMessage = "Can't write PluginManifest.json",
                errorCause = throwable.stackTrace.formatAsMultilineString(),
                errorCompanion = "The Magisk module was extracted, but Rootless Store could not write the generated PluginManifest.json."
            )
        }

        // Re-zip to _template_.zip
        runCatching {
            androidFileSystemRezipOperatorGatewayImpl.rezipFromFile(
                originPluginFile = magiskTemplateDirectory,
                targetZipFile = magiskTemplateZipFile
            )
        }.onFailure { throwable ->
            return PluginError(
                errorMessage = "Can't rezip magisk module",
                errorCause = throwable.stackTrace.formatAsMultilineString(),
                errorCompanion = "The converted Magisk module could not be compressed into the shell plugin staging archive."
            )
        }

        val isMagiskTemplateDirectoryDeleted = androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(
            magiskTemplateDirectory.path
        )
        if (!isMagiskTemplateDirectoryDeleted) {
            return PluginError(
                errorMessage = "Delete magisk template directory failed",
                errorCause = "",
                errorCompanion = "The converted package was created, but Rootless Store could not clean up the temporary Magisk template directory."
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
                errorCause = "",
                errorCompanion = "The converted shell plugin archive was ready, but Shizuku could not install it into the com.android.shell private plugin directory."
            )
        }

        if (!isMagiskTemplateArchiveDeleted) {
            return PluginError(
                errorMessage = "Delete magisk template zip failed",
                errorCause = "",
                errorCompanion = "The Magisk shell plugin installation finished, but Rootless Store could not delete the temporary staging zip."
            )
        }

        // Insert result to PluginRepository
        pluginRepositoryImpl.addPlugin(pluginManifest)
        pluginStatusRepositoryImpl.registerPluginStatus(pluginManifest.pluginId, PluginOrigin.Local)

        return null
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
