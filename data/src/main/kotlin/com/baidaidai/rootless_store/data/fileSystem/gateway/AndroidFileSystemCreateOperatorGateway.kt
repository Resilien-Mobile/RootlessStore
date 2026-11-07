package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.net.Uri
import java.io.File

interface AndroidFileSystemCreateOperatorGateway {

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

    fun copyFileToFile(
        originFile: File,
        targetFile: File
    ): File

}
