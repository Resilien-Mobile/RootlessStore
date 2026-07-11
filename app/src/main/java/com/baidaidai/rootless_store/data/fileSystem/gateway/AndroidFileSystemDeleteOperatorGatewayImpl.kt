package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AndroidFileSystemDeleteOperatorGatewayImpl @Inject constructor(
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

    // Delete FS Operator
    @Deprecated(
        message = "Recommended to use deleteDirectoryByPackageName method, instead of the deleteOneFile method",
        replaceWith = ReplaceWith("deleteDirectoryByPackageName(pluginPackageName)")
    )
    fun deleteOneFile(pluginPackageName: String): Boolean {
        val targetFile = File(getInternalPluginRootDirectory(), "${pluginPackageName}.zip")

        return targetFile.delete()
    }

    fun deleteDirectoryByPackageName(pluginPackageName: String): Boolean {
        val targetFile = File(getInternalPluginRootDirectory(), pluginPackageName)

        return targetFile.deleteRecursively()
    }

    fun deleteEnvironmentDirectoryByPackageName(environmentPackageName: String): Boolean {
        val targetFile = File(getInternalEnvironmentRootDirectory(), environmentPackageName)

        return targetFile.deleteRecursively()
    }

    fun deleteFileOrDirectory(filePath: String): Boolean {
        val targetFileOrDirectory = File(filePath)
        return targetFileOrDirectory.deleteRecursively()
    }

}
