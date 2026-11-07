package com.baidaidai.rootless_store.domain.fileSystem.gateway

import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import java.io.File

interface AndroidFileSystemDefaultOperatorGateway {

    fun getExternalAppFilesDirectoryPath(): String

    fun getExternalAppFilesDirectoryFile(): File

    fun getExternalAppCacheDirectoryPath(): String

    fun getExternalAppCacheDirectoryFile(): File

    fun getExternalAppPackageCacheDirectoryFile(): File

    fun getExternalAppMagiskDirectoryFile(): File

    fun getExternalAppMagiskTemplateDirectoryFile(): File

    fun getInternalPluginDirectoryPath(): String

    fun getInternalPluginDirectoryFile(): File

    fun getInternalFilesRootDirectoryPath(): String

    fun getInternalFilesRootDirectoryFile(): File

    fun getInternalPluginCacheDirectoryPath(): String

    fun getInternalPluginCacheDirectoryFile(): File

    fun resolvePluginEntryPoint(
        pluginManifest: PluginManifest
    ): String

    fun resolvePluginPackageDirectory(
        pluginManifest: PluginManifest
    ): String

    fun getDefaultEnvironmentDirectoryPath(): String

    fun getInternalEnvironmentCacheDirectoryFile(): File

    fun resolveEnvironmentPackageDirectory(
        environmentManifest: EnvironmentManifest
    ): String

}
