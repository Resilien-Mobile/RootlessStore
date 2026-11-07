package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.FileInputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject

class AndroidFileSystemUnzipOperatorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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

    private fun resolveChildFile(parentDirectory: File, childName: String): File {
        return File(parentDirectory, childName)
    } // /File/Plugin/PLUGIN

    // Un-Zip FS Operator
    fun unzipFromFileToDirectory(originFile: File, targetDirectory: File) {
        FileInputStream(originFile).use { unzipInputStreamToDirectory(it, targetDirectory) }
    }

    fun extractFilesFromZip(
        originFile: File,
        targetDirectory: File,
        fileNames: Set<String>
    ) {
        targetDirectory.mkdirs()
        FileInputStream(originFile).use { inputStream ->
            ZipInputStream(BufferedInputStream(inputStream)).use { zipInputStream ->
                var zipEntry = zipInputStream.nextEntry
                while (zipEntry != null) {
                    if (!zipEntry.isDirectory && zipEntry.name in fileNames) {
                        val targetFile = File(targetDirectory, zipEntry.name)
                        targetFile.parentFile?.mkdirs()
                        FileOutputStream(targetFile).use { outputStream ->
                            zipInputStream.copyTo(outputStream)
                        }
                    }
                    zipInputStream.closeEntry()
                    zipEntry = zipInputStream.nextEntry
                }
            }
        }
    }

    fun unzipFromByteChannelToDirectory(
        originFileByteChannel: ByteReadChannel,
        targetDirectory: File
    ) {
        originFileByteChannel.toInputStream().use { unzipInputStreamToDirectory(it, targetDirectory) }
    }

    private fun unzipInputStreamToDirectory(
        inputStream: InputStream,
        targetDirectory: File
    ) {
        targetDirectory.mkdirs()
        val canonicalTargetDirectory = targetDirectory.canonicalFile

        ZipInputStream(BufferedInputStream(inputStream)).use { zipInputStream ->
            var zipEntry = zipInputStream.nextEntry
            while (zipEntry != null) {
                val targetFile = File(targetDirectory, zipEntry.name).canonicalFile
                if (!targetFile.path.startsWith(canonicalTargetDirectory.path + File.separator)) {
                    zipInputStream.closeEntry()
                    zipEntry = zipInputStream.nextEntry
                    continue
                }

                if (zipEntry.isDirectory) {
                    targetFile.mkdirs()
                } else {
                    targetFile.parentFile?.mkdirs()
                    FileOutputStream(targetFile).use { outputStream ->
                        zipInputStream.copyTo(outputStream)
                    }
                }

                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
        }
    }

    fun unzipEnvironmentFromFile(
        originFile: File,
        pluginRootDirectory: File,
        directoryName: String
    ) {
        val targetDirectory = resolveChildFile(pluginRootDirectory, directoryName.trim())
        targetDirectory.mkdirs()
        FileInputStream(originFile).use { unzipInputStreamToDirectory(it, targetDirectory) }
    }

    @Suppress("UNUSED_PARAMETER")
    fun unzipFromUri(originFileByteChannel: ByteReadChannel, pluginRootDirectory: File, directoryName: String) {

        // Resolve target directory for copy
        val internalPluginRootDirectory = ensureInternalPluginRootDirectory()
        resolveChildFile(internalPluginRootDirectory, directoryName)
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
    fun unzipEnvironmentFromUri(
        originFileByteChannel: ByteReadChannel,
        pluginRootDirectory: File,
        directoryName: String
    ) {

        // Resolve target directory for copy
        val internalEnvironmentRootDirectory = ensureInternalEnvironmentRootDirectory()
        resolveChildFile(internalEnvironmentRootDirectory, directoryName)
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
