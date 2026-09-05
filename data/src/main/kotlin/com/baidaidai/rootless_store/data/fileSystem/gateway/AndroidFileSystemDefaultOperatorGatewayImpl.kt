package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AndroidFileSystemDefaultOperatorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        private const val PLUGIN_DIR_NAME = "Plugin"
        private const val ENVIRONMENT_DIR_NAME = "Environment"
        private const val MAGISK_DIR_NAME = "Magisk"
        private const val MAGISK_TEMPLATE_DIR_NAME = "template"
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

    private fun getInternalEnvironmentCacheDirectory(): File {
        return File(context.cacheDir, ENVIRONMENT_DIR_NAME)
    }

    private fun getExternalAppFilesRootDirectory(): File {
        return context.getExternalFilesDir(null)!!
    }

    private fun getExternalAppCacheRootDirectory(): File {
        return context.externalCacheDir!!
    }

    // Default FS Operator (External)
    fun getExternalAppFilesDirectoryPath(): String {
        return getExternalAppFilesRootDirectory().path
    } // /sdcard/Android/data/APP_PACKAGE/files

    fun getExternalAppFilesDirectoryFile(): File {
        return getExternalAppFilesRootDirectory()
    } // /sdcard/Android/data/APP_PACKAGE/files

    fun getExternalAppCacheDirectoryPath(): String {
        return getExternalAppCacheRootDirectory().path
    } // /sdcard/Android/data/APP_PACKAGE/cache

    fun getExternalAppCacheDirectoryFile(): File {
        return getExternalAppCacheRootDirectory()
    } // /sdcard/Android/data/APP_PACKAGE/cache

    fun getExternalAppMagiskDirectoryFile(): File {
        return File(getExternalAppFilesRootDirectory(), MAGISK_DIR_NAME)
    } // /sdcard/Android/data/APP_PACKAGE/files/magisk

    fun getExternalAppMagiskTemplateDirectoryFile(): File {
        return File(getExternalAppMagiskDirectoryFile(), MAGISK_TEMPLATE_DIR_NAME)
    } // /sdcard/Android/data/APP_PACKAGE/files/magisk/template

    // Default FS Operator (Plugin)
    fun getDefaultPluginDirectoryPath(): String {
        return getInternalPluginRootDirectory().path
    } // /File/Plugin

    fun getInternalPluginCacheDirectoryPath(): String {
        return getInternalPluginCacheDirectory().path
    } // /Cache/Plugin: String

    fun getInternalPluginCacheDirectoryFile(): File {
        return getInternalPluginCacheDirectory()
    } // /Cache/Plugin: File

    fun resolvePluginEntryPoint(pluginManifest: PluginManifest): String {
        val defaultPluginDirectoryPath = getDefaultPluginDirectoryPath()
        val pluginPackageName = pluginManifest.pluginPackageName
        val pluginEntryPoint = pluginManifest.entryPoint
        return "$defaultPluginDirectoryPath/$pluginPackageName/$pluginEntryPoint"
    } // /File/Plugin/PLUGIN/entry

    fun resolvePluginPackageDirectory(pluginManifest: PluginManifest): String {
        val defaultPluginDirectoryPath = getDefaultPluginDirectoryPath()
        val pluginPackageName = pluginManifest.pluginPackageName
        return "$defaultPluginDirectoryPath/$pluginPackageName"
    } // /File/Plugin/PLUGIN

    // Default FS Operator (Environment)
    fun getDefaultEnvironmentDirectoryPath(): String {
        return getInternalEnvironmentRootDirectory().path
    } // /File/Environment

    fun getInternalEnvironmentCacheDirectoryFile(): File {
        return getInternalEnvironmentCacheDirectory()
    } // /Cache/Environment: File

    fun resolveEnvironmentPackageDirectory(environmentManifestRoom: EnvironmentManifestRoom): String {
        val defaultEnvironmentDirectoryPath = getDefaultEnvironmentDirectoryPath()
        val environmentPackageName = environmentManifestRoom.environmentPackageName
        return "$defaultEnvironmentDirectoryPath/$environmentPackageName"
    } // /File/Environment/ENVIRONMENT

}
