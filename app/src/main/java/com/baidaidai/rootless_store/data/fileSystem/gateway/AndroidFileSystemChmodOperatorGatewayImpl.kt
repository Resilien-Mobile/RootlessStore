package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AndroidFileSystemChmodOperatorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        private const val PLUGIN_DIR_NAME = "Plugin"
        private const val ENVIRONMENT_DIR_NAME = "Environment"
    }

    private fun getInternalPluginRootDirectory(): File {
        return File(context.filesDir, PLUGIN_DIR_NAME)
    }

    private fun getInternalEnvironmentRootDirectory(): File {
        return File(context.filesDir, ENVIRONMENT_DIR_NAME)
    }

    // Chmod FS Operator
    fun setPluginEntryPointExecutable(pluginManifest: PluginManifest): Boolean {
        val pluginRootDirectory = getInternalPluginRootDirectory()
        val pluginPackageName = pluginManifest.pluginPackageName
        val pluginEntryPoint = pluginManifest.entryPoint
        val child = "$pluginPackageName/$pluginEntryPoint"
        return File(pluginRootDirectory, child).setExecutable(true)
    }

    fun setEnvironmentEntryPointExecutable(environmentManifest: EnvironmentManifest): Boolean {
        val environmentRootDirectory = getInternalEnvironmentRootDirectory()
        val environmentPackageName = environmentManifest.environmentPackageName
        val environmentEntryPoint = environmentManifest.entryPoint
        val child = "$environmentPackageName/$environmentEntryPoint"
        return File(environmentRootDirectory, child).setExecutable(true)
    }

}
