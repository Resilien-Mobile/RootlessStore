package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import android.net.Uri
import android.util.Log
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestRoom
import dagger.hilt.android.qualifiers.ApplicationContext
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject

class AndroidFileSystemCapabilityGatewayImpl @Inject constructor(
    @ApplicationContext val context: Context
){
    private companion object {
        private const val PLUGIN_DIR_NAME = "Plugin"
        private const val ENVIRONMENT_DIR_NAME = "Environment"
        private const val PLUGIN_MANIFEST_FILE_NAME = "PluginManifest.json"
        private const val ENVIRONMENT_MANIFEST_FILE_NAME = "EnvironmentManifest.json"
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

    // Default FS Operator
    fun getDefaultPluginDirectoryPath(): String{
        return getInternalPluginRootDirectory().path
    } // /File/Plugin
    fun getPluginEntryPoint(pluginManifestRoom: PluginManifestRoom): String{
        val defaultPluginDirectoryPath = getDefaultPluginDirectoryPath()
        val pluginPackageName = pluginManifestRoom.pluginPackageName
        val pluginEntryPoint = pluginManifestRoom.entryPoint
        return "$defaultPluginDirectoryPath/$pluginPackageName/$pluginEntryPoint"
    }  // /File/Plugin/PLUGIN/entry
    fun getPluginPackageDirectory(pluginManifestRoom: PluginManifestRoom): String {
        val defaultPluginDirectoryPath = getDefaultPluginDirectoryPath()
        val pluginPackageName = pluginManifestRoom.pluginPackageName
        return "$defaultPluginDirectoryPath/$pluginPackageName"
    }  // /File/Plugin/PLUGIN

    // Search FS Operator
    fun confirmPluginPathExists(): Boolean{
        return confirmPathExists(PLUGIN_DIR_NAME)
    }  // /File/Plugin?
    fun confirmEnvironmentPathExists(): Boolean{
        return confirmPathExists(ENVIRONMENT_DIR_NAME)
    }  // /File/Environment?
    private fun confirmPathExists(path: String): Boolean{
        val targetFile = File(context.filesDir, path)
        Log.d("confirmPathExists", targetFile.exists().toString())
        return targetFile.exists()
    }  // /File/?

    // Create FS Operator
    fun createFileDir(path: String){
        if (!confirmPathExists(path)){
            File(context.filesDir, path).mkdirs()
        }
    }  // /File
    @Deprecated(
        message = "Not Longer Recommended",
        replaceWith = ReplaceWith("createVoidFileDirectory(pluginRootDirectory, directoryName)")
    )
    fun createOneVoidFile(destination: File, fileName: String): Boolean{
        val result = File(destination, "$fileName.zip").createNewFile()  // 创建了文件，而非单纯路径
        return result
    }
    fun createVoidFileDirectory(pluginRootDirectory: File, directoryName: String): File {
        return File(pluginRootDirectory, directoryName) // 创建文件夹
    }  // /File/Plugin/PLUGIN

    // Deprecated FS Operator
    @Deprecated(
        message = "Recommended to use unZipFromFile method, instead of the copyFile method",
        replaceWith = ReplaceWith("unzipFromFile(originFileURI, pluginRootDirectory, directoryName)")
    )
    fun copyFile(originFileURI: Uri, destination: File, destinationFileName: String? = null) {

        // Get file's name, always powered by readManiFestJsonContent
        val fileName = when {
            !destinationFileName.isNullOrBlank() -> destinationFileName.trim()  // 只有destinationFilName显式指定，否则不走
            else -> {
                readRawPluginManifest(originFileURI).let { json ->
                    readManifestJsonContent(json).pluginPackageName
                }.trim()
            }
        }

        // Provide void file, for copy use
        val internalDestination = ensureInternalPluginRootDirectory()
        createOneVoidFile(internalDestination,fileName)  // needs prevent override files
        val operationFile = File(internalDestination, "$fileName.zip")

        // The core of copy operator
        context.contentResolver.openInputStream(originFileURI).use { input ->
            FileOutputStream(operationFile).use { output ->
                input!!.copyTo(output)
            }
        }
    }
    @Deprecated(
        message = "Recommended to use unZipFromURI method, instead of the copyFile method",
        replaceWith = ReplaceWith("unZipFromURI(originFileByteChannel, pluginRootDirectory, directoryName)")
    )
    fun copyFile(originFileByteChannel: ByteReadChannel, destination: File, destinationFileName: String) {

        // Get file's name, always powered by readManiFestJsonContent


        // Provide void file, for copy use
        val internalDestination = ensureInternalPluginRootDirectory()
        createOneVoidFile(internalDestination, destinationFileName)  // needs prevent override files
        val operationFile = File(internalDestination, "$destinationFileName.zip")

        // The core of copy operator
        FileOutputStream(operationFile).use { out ->
            originFileByteChannel.toInputStream().use { input ->
                input.copyTo(out)
            }
        }
    }

    // Un-Zip FS Operator
    @Suppress("UNUSED_PARAMETER")
    fun unzipFromFile(originFileURI: Uri, pluginRootDirectory: File, directoryName: String? = null) {

        // Get file's name, always powered by readManiFestJsonContent
        val directoryName = when {

            !directoryName.isNullOrBlank() -> {
                directoryName.trim()

            }  // 只有destinationFilName显式指定，否则不走

            else -> {
                readRawPluginManifest(originFileURI).let { json ->
                    readManifestJsonContent(json).pluginPackageName
                }.trim()
            }

        }

        // Create Void Directory
        val internalPluginRootDirectory = ensureInternalPluginRootDirectory()
        val createdFileDirectory = createVoidFileDirectory(internalPluginRootDirectory, directoryName).apply {
            mkdirs()
        }

        // Open IO Stream
        context.contentResolver.openInputStream(originFileURI).use{ fis ->
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

            }  // 只有destinationFilName显式指定，否则不走

            else -> {
                readRawEnvironmentManifest(originFileURI).let { json ->
                    readEnvironmentManifestJsonContent(json).environmentPackageName
                }.trim()
            }

        }

        // Create Void Directory
        val internalEnvironmentRootDirectory = ensureInternalEnvironmentRootDirectory()
        val createdFileDirectory = createVoidFileDirectory(internalEnvironmentRootDirectory, directoryName).apply {
            mkdirs()
        }

        // Open IO Stream
        context.contentResolver.openInputStream(originFileURI).use{ fis ->
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
    fun unZipFromURI(originFileByteChannel: ByteReadChannel, pluginRootDirectory: File, directoryName: String){

        // Provide void file, for copy use
        val internalPluginRootDirectory = ensureInternalPluginRootDirectory()
        createVoidFileDirectory(internalPluginRootDirectory, directoryName)  // needs prevent override files
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
    fun unZipEnvironmentFromURI(originFileByteChannel: ByteReadChannel, pluginRootDirectory: File, directoryName: String){

        // Provide void file, for copy use
        val internalEnvironmentRootDirectory = ensureInternalEnvironmentRootDirectory()
        createVoidFileDirectory(internalEnvironmentRootDirectory, directoryName)  // needs prevent override files
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

    // Read FS Operator
    private fun readRawManifest(uri: Uri, manifestFileName: String): String? {
        context.contentResolver.openInputStream(uri).use { inputStream ->
//            if (inputStream == null) {
//                Log.e("readZipContent", "openInputStream returned null, uri=$uri")
//                return
//            }

            ZipInputStream(BufferedInputStream(inputStream)).use { zipInputStream ->
                /**
                 * The entry is like relative path, but root path is zipFile/...
                 */
                var zipEntry = zipInputStream.nextEntry
                while (zipEntry != null) {
                    val entryPath = zipEntry.name                       // 可能是 "a/b/PluginManifest.json"
                    val fileNameOnly = entryPath.substringAfterLast('/') // 取最后一级文件名
                    val isTarget = !zipEntry.isDirectory &&
                        fileNameOnly.equals(manifestFileName, ignoreCase = true)

                    if (isTarget) {
                        // 读取当前 entry 的内容（这里用 readBytes，适合 manifest 这种小文件）
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

    fun readRawPluginManifest(uri: Uri): String{
        return readRawManifest(uri, PLUGIN_MANIFEST_FILE_NAME) ?: ""
    }  // Get JSON File
    fun readRawEnvironmentManifest(uri: Uri): String{
        return readRawManifest(uri, ENVIRONMENT_MANIFEST_FILE_NAME) ?: ""
    }  // Get JSON File

    fun readManifestJsonContent(jsonContent: String): PluginManifestLocal {
        val json = Json {
            ignoreUnknownKeys = true // JSON 多字段也不炸
            isLenient = true
        }
        val manifest: PluginManifestLocal = json.decodeFromString(PluginManifestLocal.Companion.serializer(),jsonContent)
        return manifest
    }  // Convert JSON to PluginManifestLocal
    fun readEnvironmentManifestJsonContent(jsonContent: String): EnvironmentManifestLocal {
        val json = Json {
            ignoreUnknownKeys = true // JSON 多字段也不炸
            isLenient = true
        }
        val manifest: EnvironmentManifestLocal = json.decodeFromString(EnvironmentManifestLocal.Companion.serializer(),jsonContent)
        return manifest
    }  // Convert JSON to EnvironmentManifestLocal

    // Delete FS Operator
    @Deprecated(
        message = "Recommended to use deleteDirectoryByPackageName method, instead of the deleteOneFile method",
        replaceWith = ReplaceWith("deleteDirectoryByPackageName(pluginPackageName)")
    )
    fun deleteOneFile(pluginPackageName: String): Boolean{
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

    // Chmod FS Operator
    fun setPluginEntryPointExecutable(pluginManifestRoom: PluginManifestRoom): Boolean{
        val pluginRootDirectory = getInternalPluginRootDirectory()
        val pluginPackageName = pluginManifestRoom.pluginPackageName
        val pluginEntryPoint = pluginManifestRoom.entryPoint
        val _child = "$pluginPackageName/$pluginEntryPoint"
        return File(pluginRootDirectory,_child).setExecutable(true)
    }
    fun setEnvironmentEntryPointExecutable(environmentManifestRoom: EnvironmentManifestRoom): Boolean{
        val environmentRootDirectory = getInternalEnvironmentRootDirectory()
        val environmentPackageName = environmentManifestRoom.environmentPackageName
        val environmentEntryPoint = environmentManifestRoom.entryPoint
        val _child = "$environmentPackageName/$environmentEntryPoint"
        return File(environmentRootDirectory,_child).setExecutable(true)
    }
}
