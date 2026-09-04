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
    fun ensureFilesDirectory(path: String) {
        if (!hasInternalDirectory(path)) {
            File(context.filesDir, path).mkdirs()
        }
    } // /File

    fun ensureCacheDirectory(path: String) {
        if (!hasCacheDirectory(path)) {
            File(context.cacheDir, path).mkdirs()
        }
    } // /Cache/path  e.g. /Cache/Plugin

    @Deprecated(
        message = "Not Longer Recommended",
        replaceWith = ReplaceWith("resolveChildFile(destinationDirectory, fileNameWithoutExtension + \".zip\").createNewFile()")
    )
    fun createEmptyZipFile(destinationDirectory: File, fileNameWithoutExtension: String): Boolean {
        val isZipFileCreated = File(destinationDirectory, "$fileNameWithoutExtension.zip").createNewFile()
        return isZipFileCreated
    }

    fun resolveChildFile(parentDirectory: File, childName: String): File {
        return File(parentDirectory, childName)
    } // /File/Plugin/PLUGIN

    fun writeTextFile(parentDirectory: File, fileName: String, content: String): File {
        val targetFile = File(parentDirectory, fileName)
        targetFile.writeText(content)
        return targetFile
    }

    fun copyUriToFile(originFileUri: Uri, targetFile: File): File {
        targetFile.parentFile?.mkdirs()

        context.contentResolver.openInputStream(originFileUri).use { originInputStream ->
            FileOutputStream(targetFile).use { targetOutputStream ->
                originInputStream!!.copyTo(targetOutputStream)
            }
        }

        return targetFile
    }

    private fun hasInternalDirectory(path: String): Boolean {
        val targetFile = File(context.filesDir, path)
        return targetFile.exists()
    } // /File/?

    private fun hasCacheDirectory(path: String): Boolean {
        val targetFile = File(context.cacheDir, path)
        return targetFile.exists()
    } // /Cache/?

}
