package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import android.net.Uri
import android.util.Log
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject

class AndroidFileSystemReadOperatorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        private const val PLUGIN_MANIFEST_FILE_NAME = "PluginManifest.json"
        private const val ENVIRONMENT_MANIFEST_FILE_NAME = "EnvironmentManifest.json"
        private const val MAGISK_MODULE_PROP_FILE_NAME = "module.prop"
    }

    // Read FS Operator
    private fun loadRawManifest(uri: Uri, manifestFileName: String): String? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        return inputStream.use { loadRawManifest(it, manifestFileName) }
    }

    private fun loadRawManifest(file: File, manifestFileName: String): String? {
        return file.inputStream().use { loadRawManifest(it, manifestFileName) }
    }

    private fun loadRawManifest(inputStream: InputStream, manifestFileName: String): String? {
        ZipInputStream(BufferedInputStream(inputStream)).use { zipInputStream ->
            var zipEntry = zipInputStream.nextEntry
            while (zipEntry != null) {
                val entryPath = zipEntry.name
                val fileNameOnly = entryPath.substringAfterLast('/')
                val isTarget = !zipEntry.isDirectory &&
                    fileNameOnly.equals(manifestFileName, ignoreCase = true)

                if (isTarget) {
                    val content = zipInputStream.readBytes().toString(Charsets.UTF_8)
                    Log.d("readZipContent", "$manifestFileName content: $content")
                    zipInputStream.closeEntry()
                    return content
                }

                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }

            Log.w("readZipContent", "$manifestFileName not found")
            return null
        }
    }

    fun hasFileInZip(uri: Uri, fileName: String): Boolean {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return false
        return inputStream.use { hasFileInZip(it, fileName) }
    } // Confirm File Exists in Zip

    fun hasFileInZip(file: File, fileName: String): Boolean {
        return file.inputStream().use { hasFileInZip(it, fileName) }
    }

    private fun hasFileInZip(inputStream: InputStream, fileName: String): Boolean {
        ZipInputStream(BufferedInputStream(inputStream)).use { zipInputStream ->
            var zipEntry = zipInputStream.nextEntry
            while (zipEntry != null) {
                val fileNameOnly = zipEntry.name.substringAfterLast('/')
                if (!zipEntry.isDirectory && fileNameOnly.equals(fileName, ignoreCase = true)) {
                    zipInputStream.closeEntry()
                    return true
                }
                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
            return false
        }
    }

    fun loadBytesInZip(file: File, fileName: String): ByteArray? {
        return file.inputStream().use { loadBytesInZip(it, fileName) }
    }

    private fun loadBytesInZip(inputStream: InputStream, fileName: String): ByteArray? {
        ZipInputStream(BufferedInputStream(inputStream)).use { zipInputStream ->
            var zipEntry = zipInputStream.nextEntry
            while (zipEntry != null) {
                val fileNameOnly = zipEntry.name.substringAfterLast('/')
                if (!zipEntry.isDirectory && fileNameOnly.equals(fileName, ignoreCase = true)) {
                    val fileBytes = zipInputStream.readBytes()
                    zipInputStream.closeEntry()
                    return fileBytes
                }
                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
            return null
        }
    }

    fun loadRawMagiskModuleProp(uri: Uri): String {
        return loadRawManifest(uri, MAGISK_MODULE_PROP_FILE_NAME) ?: ""
    } // Get Magisk module.prop File

    fun loadRawMagiskModuleProp(file: File): String {
        return loadRawManifest(file, MAGISK_MODULE_PROP_FILE_NAME) ?: ""
    }

    fun loadRawPluginManifest(uri: Uri): String {
        return loadRawManifest(uri, PLUGIN_MANIFEST_FILE_NAME) ?: ""
    } // Get JSON File

    fun loadRawPluginManifest(file: File): String {
        return loadRawManifest(file, PLUGIN_MANIFEST_FILE_NAME) ?: ""
    }

    fun loadRawEnvironmentManifest(uri: Uri): String {
        return loadRawManifest(uri, ENVIRONMENT_MANIFEST_FILE_NAME) ?: ""
    } // Get JSON File

    fun loadRawEnvironmentManifest(file: File): String {
        return loadRawManifest(file, ENVIRONMENT_MANIFEST_FILE_NAME) ?: ""
    }

    fun loadFileContent(filePath: String): String {
        return File(filePath).readText()
    }

    fun parsePluginManifest(jsonContent: String): PluginManifest {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        return json.decodeFromString<PluginManifest>(jsonContent)
    } // Convert JSON to PluginManifest

    fun parseEnvironmentManifest(jsonContent: String): EnvironmentManifest {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        return json.decodeFromString<EnvironmentManifest>(jsonContent)
    } // Convert JSON to EnvironmentManifest

}
