package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject

class AndroidFileSystemUnZipOperatorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val androidFileSystemReadOperatorGatewayImpl: AndroidFileSystemReadOperatorGatewayImpl
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

    private fun ensureInternalPluginRootDirectory(): File {
        return getInternalPluginRootDirectory().apply { mkdirs() }
    }

    private fun ensureInternalEnvironmentRootDirectory(): File {
        return getInternalEnvironmentRootDirectory().apply { mkdirs() }
    }

    private fun createVoidFileDirectory(pluginRootDirectory: File, directoryName: String): File {
        return File(pluginRootDirectory, directoryName)
    } // /File/Plugin/PLUGIN

    // Un-Zip FS Operator
    @Suppress("UNUSED_PARAMETER")
    fun unzipFromFile(originFileURI: Uri, pluginRootDirectory: File, directoryName: String? = null) {

        // Get file's name, always powered by readManiFestJsonContent
        val directoryName = when {

            !directoryName.isNullOrBlank() -> {
                directoryName.trim()
            }

            else -> {
                androidFileSystemReadOperatorGatewayImpl.readRawPluginManifest(originFileURI).let { json ->
                    androidFileSystemReadOperatorGatewayImpl.readManifestJsonContent(json).pluginPackageName
                }.trim()
            }
        }

        // Create Void Directory
        val internalPluginRootDirectory = ensureInternalPluginRootDirectory()
        val createdFileDirectory = createVoidFileDirectory(internalPluginRootDirectory, directoryName).apply {
            mkdirs()
        }

        // Open IO Stream
        context.contentResolver.openInputStream(originFileURI).use { fis ->
            // Unzip from File Input Stream
            ZipInputStream(BufferedInputStream(fis)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val outFile = File(createdFileDirectory, entry.name)

                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        FileOutputStream(outFile).use { out ->
                            zis.copyTo(out)
                        }
                    }

                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun unzipEnvironmentFromFile(originFileURI: Uri, pluginRootDirectory: File, directoryName: String? = null) {

        // Get file's name, always powered by readManiFestJsonContent
        val directoryName = when {

            !directoryName.isNullOrBlank() -> {
                directoryName.trim()
            }

            else -> {
                androidFileSystemReadOperatorGatewayImpl.readRawEnvironmentManifest(originFileURI).let { json ->
                    androidFileSystemReadOperatorGatewayImpl.readEnvironmentManifestJsonContent(json).environmentPackageName
                }.trim()
            }
        }

        // Create Void Directory
        val internalEnvironmentRootDirectory = ensureInternalEnvironmentRootDirectory()
        val createdFileDirectory = createVoidFileDirectory(internalEnvironmentRootDirectory, directoryName).apply {
            mkdirs()
        }

        // Open IO Stream
        context.contentResolver.openInputStream(originFileURI).use { fis ->
            // Unzip from File Input Stream
            ZipInputStream(BufferedInputStream(fis)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val outFile = File(createdFileDirectory, entry.name)

                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        FileOutputStream(outFile).use { out ->
                            zis.copyTo(out)
                        }
                    }

                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun unZipFromURI(originFileByteChannel: ByteReadChannel, pluginRootDirectory: File, directoryName: String) {

        // Provide void file, for copy use
        val internalPluginRootDirectory = ensureInternalPluginRootDirectory()
        createVoidFileDirectory(internalPluginRootDirectory, directoryName)
        val operationFile = File(internalPluginRootDirectory, directoryName).apply {
            mkdirs()
        }

        // The core of copy operator
        originFileByteChannel.toInputStream().use { input ->
            ZipInputStream(BufferedInputStream(input)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val outFile = File(operationFile, entry.name)

                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        FileOutputStream(outFile).use { out ->
                            zis.copyTo(out)
                        }
                    }

                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun unZipEnvironmentFromURI(
        originFileByteChannel: ByteReadChannel,
        pluginRootDirectory: File,
        directoryName: String
    ) {

        // Provide void file, for copy use
        val internalEnvironmentRootDirectory = ensureInternalEnvironmentRootDirectory()
        createVoidFileDirectory(internalEnvironmentRootDirectory, directoryName)
        val operationFile = File(internalEnvironmentRootDirectory, directoryName).apply {
            mkdirs()
        }

        // The core of copy operator
        originFileByteChannel.toInputStream().use { input ->
            ZipInputStream(BufferedInputStream(input)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val outFile = File(operationFile, entry.name)

                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        FileOutputStream(outFile).use { out ->
                            zis.copyTo(out)
                        }
                    }

                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }
    }

}
