package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AndroidFileSystemCreateOperatorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Create FS Operator
    fun createFileDir(path: String) {
        if (!confirmPathExists(path)) {
            File(context.filesDir, path).mkdirs()
        }
    } // /File

    fun createCacheDir(path: String) {
        if (!confirmCacheExists(path)) {
            File(context.cacheDir, path).mkdirs()
        }
    } // /Cache/path  e.g. /Cache/Plugin

    @Deprecated(
        message = "Not Longer Recommended",
        replaceWith = ReplaceWith("createVoidFileDirectory(pluginRootDirectory, directoryName)")
    )
    fun createVoidFile(destination: File, fileName: String): Boolean {
        val result = File(destination, "$fileName.zip").createNewFile()
        return result
    }

    fun createVoidFileDirectory(pluginRootDirectory: File, directoryName: String): File {
        return File(pluginRootDirectory, directoryName)
    } // /File/Plugin/PLUGIN

    fun writeTextFile(parentDirectory: File, fileName: String, content: String): File {
        val targetFile = File(parentDirectory, fileName)
        targetFile.writeText(content)
        return targetFile
    }

    fun copyUriToFile(originFileURI: Uri, targetFile: File): File {
        targetFile.parentFile?.mkdirs()

        context.contentResolver.openInputStream(originFileURI).use { originInputStream ->
            FileOutputStream(targetFile).use { targetOutputStream ->
                originInputStream!!.copyTo(targetOutputStream)
            }
        }

        return targetFile
    }

    private fun confirmPathExists(path: String): Boolean {
        val targetFile = File(context.filesDir, path)
        return targetFile.exists()
    } // /File/?

    private fun confirmCacheExists(path: String): Boolean {
        val targetFile = File(context.cacheDir, path)
        return targetFile.exists()
    } // /Cache/?

}
