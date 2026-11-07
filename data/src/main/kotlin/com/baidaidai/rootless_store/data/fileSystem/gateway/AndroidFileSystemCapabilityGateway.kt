package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.net.Uri
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import io.ktor.utils.io.ByteReadChannel
import java.io.File

interface AndroidFileSystemCapabilityGateway {

    fun getDefaultPluginDirectoryPath(): String

    fun getCachePluginDirectoryPath(): String

    fun getCachePluginDirectoryFile(): File

    fun getShellPluginStagingDirectoryFile(): File

    fun resolvePluginEntryPoint(
        pluginManifest: PluginManifest
    ): String

    fun resolvePluginPackageDirectory(
        pluginManifest: PluginManifest
    ): String

    fun getDefaultEnvironmentDirectoryPath(): String

    fun getCacheEnvironmentDirectoryFile(): File

    fun resolveEnvironmentPackageDirectory(
        environmentManifest: EnvironmentManifest
    ): String

    fun hasPluginDirectory(): Boolean

    fun hasPluginCacheDirectory(): Boolean

    fun hasEnvironmentDirectory(): Boolean

    fun hasEnvironmentCacheDirectory(): Boolean

    fun ensureFilesDirectory(
        path: String
    )

    fun ensureCacheDirectory(
        path: String
    )

    fun createEmptyZipFile(
        destinationDirectory: File,
        fileNameWithoutExtension: String
    ): Boolean

    fun resolveChildFile(
        parentDirectory: File,
        childName: String
    ): File

    fun writeTextFile(
        parentDirectory: File,
        fileName: String,
        content: String
    ): File

    fun copyUriToFile(
        originFileUri: Uri,
        targetFile: File
    ): File

    fun unzipFromFile(
        originFileUri: Uri,
        pluginRootDirectory: File,
        directoryName: String? = null
    )

    fun unzipEnvironmentFromFile(
        originFileUri: Uri,
        pluginRootDirectory: File,
        directoryName: String? = null
    )

    fun unzipFromUri(
        originFileByteChannel: ByteReadChannel,
        pluginRootDirectory: File,
        directoryName: String
    )

    fun unzipEnvironmentFromUri(
        originFileByteChannel: ByteReadChannel,
        pluginRootDirectory: File,
        directoryName: String
    )

    fun rezipFromFile(
        originPluginFile: File,
        targetZipFile: File
    )

    fun loadRawPluginManifest(
        uri: Uri
    ): String

    fun loadRawEnvironmentManifest(
        uri: Uri
    ): String

    fun loadFileContent(
        filePath: String
    ): String

    fun parsePluginManifest(
        jsonContent: String
    ): PluginManifest

    fun parseEnvironmentManifest(
        jsonContent: String
    ): EnvironmentManifest

    fun deleteFile(
        pluginPackageName: String
    ): Boolean

    fun deleteDirectoryByPackageName(
        pluginPackageName: String
    ): Boolean

    fun deleteEnvironmentDirectoryByPackageName(
        environmentPackageName: String
    ): Boolean

    fun deleteFileOrDirectory(
        filePath: String
    ): Boolean

    fun setPluginEntryPointExecutable(
        pluginManifest: PluginManifest
    ): Boolean

    fun setEnvironmentEntryPointExecutable(
        environmentManifest: EnvironmentManifest
    ): Boolean

    fun resolveShareUriFromFile(
        file: File
    ): Uri

}
