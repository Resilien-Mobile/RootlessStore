package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AndroidFileSystemDefaultOperatorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        private const val PLUGIN_DIR_NAME = "Plugin"
        private const val ENVIRONMENT_DIR_NAME = "Environment"
    }

    private fun getInternalPluginRootDirectory(): File {
        return File(context.filesDir, PLUGIN_DIR_NAME)
    }

    private fun getInternalPluginCacheDirectory(): File {
        return File(context.cacheDir, PLUGIN_DIR_NAME)
    }

    private fun getInternalEnvironmentRootDirectory(): File {
        return File(context.filesDir, ENVIRONMENT_DIR_NAME)
    }

    private fun getEnvironmentCacheDirectory(): File {
        return File(context.cacheDir, ENVIRONMENT_DIR_NAME)
    }

    // Default FS Operator (Plugin)
    fun getDefaultPluginDirectoryPath(): String {
        return getInternalPluginRootDirectory().path
    } // /File/Plugin

    fun getCachePluginDirectoryPath(): String {
        return getInternalPluginCacheDirectory().path
    } // /Cache/Plugin: String

    fun getCachePluginDirectoryFile(): File {
        return getInternalPluginCacheDirectory()
    } // /Cache/Plugin: File

    fun getShellPluginStagingDirectoryFile(): File {
        val shellPluginStagingDirectory = File("/sdcard/RootlessStore")
        return shellPluginStagingDirectory
    } // /sdcard/RootlessStore

    fun getPluginEntryPoint(pluginManifestRoom: PluginManifestRoom): String {
        val defaultPluginDirectoryPath = getDefaultPluginDirectoryPath()
        val pluginPackageName = pluginManifestRoom.pluginPackageName
        val pluginEntryPoint = pluginManifestRoom.entryPoint
        return "$defaultPluginDirectoryPath/$pluginPackageName/$pluginEntryPoint"
    } // /File/Plugin/PLUGIN/entry

    fun getPluginPackageDirectory(pluginManifestRoom: PluginManifestRoom): String {
        val defaultPluginDirectoryPath = getDefaultPluginDirectoryPath()
        val pluginPackageName = pluginManifestRoom.pluginPackageName
        return "$defaultPluginDirectoryPath/$pluginPackageName"
    } // /File/Plugin/PLUGIN

    // Default FS Operator (Environment)
    fun getDefaultEnvironmentDirectoryPath(): String {
        return getInternalEnvironmentRootDirectory().path
    } // /File/Environment

    fun getCacheEnvironmentDirectoryFile(): File {
        return getEnvironmentCacheDirectory()
    } // /Cache/Environment: File

    fun getEnvironmentPackageDirectory(environmentManifestRoom: EnvironmentManifestRoom): String {
        val defaultEnvironmentDirectoryPath = getDefaultEnvironmentDirectoryPath()
        val environmentPackageName = environmentManifestRoom.environmentPackageName
        return "$defaultEnvironmentDirectoryPath/$environmentPackageName"
    } // /File/Environment/ENVIRONMENT

}
