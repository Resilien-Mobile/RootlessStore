package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AndroidFileSystemSearchOperatorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        private const val PLUGIN_DIR_NAME = "Plugin"
        private const val ENVIRONMENT_DIR_NAME = "Environment"
    }

    // Search FS Operator
    fun hasPluginDirectory(): Boolean {
        return hasInternalDirectory(PLUGIN_DIR_NAME)
    } // /File/Plugin?

    fun hasPluginCacheDirectory(): Boolean {
        return hasCacheDirectory(PLUGIN_DIR_NAME)
    } // /Cache/Plugin?

    fun hasEnvironmentDirectory(): Boolean {
        return hasInternalDirectory(ENVIRONMENT_DIR_NAME)
    } // /File/Environment?

    fun hasEnvironmentCacheDirectory(): Boolean {
        return hasCacheDirectory(ENVIRONMENT_DIR_NAME)
    } // /Cache/Environment?

    private fun hasInternalDirectory(path: String): Boolean {
        val targetFile = File(context.filesDir, path)
        Log.d("hasInternalDirectory", targetFile.exists().toString())
        return targetFile.exists()
    } // /File/?

    private fun hasCacheDirectory(path: String): Boolean {
        val targetFile = File(context.cacheDir, path)
        Log.d("hasCacheDirectory", targetFile.exists().toString())
        return targetFile.exists()
    } // /Cache/?

}
