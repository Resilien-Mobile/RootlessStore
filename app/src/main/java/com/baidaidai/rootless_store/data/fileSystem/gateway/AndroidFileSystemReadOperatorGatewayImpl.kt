package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import android.net.Uri
import android.util.Log
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.File
import java.util.zip.ZipInputStream
import javax.inject.Inject

class AndroidFileSystemReadOperatorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        private const val PLUGIN_MANIFEST_FILE_NAME = "PluginManifest.json"
        private const val ENVIRONMENT_MANIFEST_FILE_NAME = "EnvironmentManifest.json"
    }

    // Read FS Operator
    private fun readRawManifest(uri: Uri, manifestFileName: String): String? {
        context.contentResolver.openInputStream(uri).use { inputStream ->
            ZipInputStream(BufferedInputStream(inputStream)).use { zipInputStream ->
                /**
                 * The entry is like relative path, but root path is zipFile/...
                 */
                var zipEntry = zipInputStream.nextEntry
                while (zipEntry != null) {
                    val entryPath = zipEntry.name
                    val fileNameOnly = entryPath.substringAfterLast('/')
                    val isTarget = !zipEntry.isDirectory &&
                        fileNameOnly.equals(manifestFileName, ignoreCase = true)

                    if (isTarget) {
                        val json = zipInputStream.readBytes().toString(Charsets.UTF_8)
                        Log.d("readZipContent", "$manifestFileName content: $json")

                        zipInputStream.closeEntry()
                        return json
                    }

                    zipInputStream.closeEntry()
                    zipEntry = zipInputStream.nextEntry
                }

                Log.w("readZipContent", "$manifestFileName not found, uri=$uri")
                return null
            }
        }
    }

    fun readRawPluginManifest(uri: Uri): String {
        return readRawManifest(uri, PLUGIN_MANIFEST_FILE_NAME) ?: ""
    } // Get JSON File

    fun readRawEnvironmentManifest(uri: Uri): String {
        return readRawManifest(uri, ENVIRONMENT_MANIFEST_FILE_NAME) ?: ""
    } // Get JSON File

    fun readFileContent(filePath: String): String {
        return File(filePath).readText()
    }

    fun readManifestJsonContent(jsonContent: String): PluginManifestLocal {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        val manifest: PluginManifestLocal = json.decodeFromString(
            PluginManifestLocal.Companion.serializer(),
            jsonContent
        )
        return manifest
    } // Convert JSON to PluginManifestLocal

    fun readEnvironmentManifestJsonContent(jsonContent: String): EnvironmentManifestLocal {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        val manifest: EnvironmentManifestLocal = json.decodeFromString(
            EnvironmentManifestLocal.Companion.serializer(),
            jsonContent
        )
        return manifest
    } // Convert JSON to EnvironmentManifestLocal

}
