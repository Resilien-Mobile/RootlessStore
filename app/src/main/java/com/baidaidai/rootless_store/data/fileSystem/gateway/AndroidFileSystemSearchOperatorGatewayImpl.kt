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
    fun confirmPluginPathExists(): Boolean {
        return confirmPathExists(PLUGIN_DIR_NAME)
    } // /File/Plugin?

    fun confirmPluginCacheExists(): Boolean {
        return confirmCacheExists(PLUGIN_DIR_NAME)
    } // /Cache/Plugin?

    fun confirmEnvironmentPathExists(): Boolean {
        return confirmPathExists(ENVIRONMENT_DIR_NAME)
    } // /File/Environment?

    fun confirmEnvironmentCacheExists(): Boolean {
        return confirmCacheExists(ENVIRONMENT_DIR_NAME)
    } // /Cache/Environment?

    private fun confirmPathExists(path: String): Boolean {
        val targetFile = File(context.filesDir, path)
        Log.d("confirmPathExists", targetFile.exists().toString())
        return targetFile.exists()
    } // /File/?

    private fun confirmCacheExists(path: String): Boolean {
        val targetFile = File(context.cacheDir, path)
        Log.d("confirmCacheExists", targetFile.exists().toString())
        return targetFile.exists()
    } // /Cache/?

}
