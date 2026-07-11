package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestLocal
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import dagger.hilt.android.qualifiers.ApplicationContext
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
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
    private fun getInternalPluginCacheDirectory(): File {
        return File(context.cacheDir, PLUGIN_DIR_NAME)
    }
    private fun getInternalEnvironmentRootDirectory(): File {
        return File(context.filesDir, ENVIRONMENT_DIR_NAME)
    }
    private fun getEnvironmentCacheDirectory(): File {
        return File( context.cacheDir,ENVIRONMENT_DIR_NAME)
    }
    private fun ensureInternalPluginRootDirectory(): File {
        return getInternalPluginRootDirectory().apply { mkdirs() }
    }
    private fun ensureInternalEnvironmentRootDirectory(): File {
        return getInternalEnvironmentRootDirectory().apply { mkdirs() }
    }

    // Default FS Operator (Plugin)
    fun getDefaultPluginDirectoryPath(): String{
        return getInternalPluginRootDirectory().path
    } // /File/Plugin
    fun getCachePluginDirectoryPath(): String{
        return getInternalPluginCacheDirectory().path
    } // /Cache/Plugin: String
    fun getCachePluginDirectoryFile(): File{
        return getInternalPluginCacheDirectory()
    } // /Cache/Plugin: File
    fun getShellPluginStagingDirectoryFile(): File {
        val shellPluginStagingDirectory = File("/sdcard/RootlessStore")
        return shellPluginStagingDirectory
    } // /sdcard/RootlessStore
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

    // Default FS Operator (Environment)
    fun getDefaultEnvironmentDirectoryPath(): String{
        return getInternalEnvironmentRootDirectory().path
    } // /File/Environment
    fun getCacheEnvironmentDirectoryFile(): File{
        return getEnvironmentCacheDirectory()
    } // /Cache/Environment: File
    fun getEnvironmentPackageDirectory(environmentManifestRoom: EnvironmentManifestRoom): String {
        val defaultEnvironmentDirectoryPath = getDefaultEnvironmentDirectoryPath()
        val environmentPackageName = environmentManifestRoom.environmentPackageName
        return "$defaultEnvironmentDirectoryPath/$environmentPackageName"
    }  // /File/Environment/ENVIRONMENT

    // Search FS Operator
    fun confirmPluginPathExists(): Boolean{
        return confirmPathExists(PLUGIN_DIR_NAME)
    }  // /File/Plugin?
    fun confirmPluginCacheExists(): Boolean{
        return confirmCacheExists(PLUGIN_DIR_NAME)  // Cache Directory's name is same
    }  // /Cache/Plugin?
    fun confirmEnvironmentPathExists(): Boolean{
        return confirmPathExists(ENVIRONMENT_DIR_NAME)
    }  // /File/Environment?
    fun confirmEnvironmentCacheExists(): Boolean{
        return confirmCacheExists(ENVIRONMENT_DIR_NAME)
    }  // /Cache/Environment?
    private fun confirmPathExists(path: String): Boolean{
        val targetFile = File(context.filesDir, path)
        Log.d("confirmPathExists", targetFile.exists().toString())
        return targetFile.exists()
    }  // /File/?
    private fun confirmCacheExists(path: String): Boolean{
        val targetFile = File(context.cacheDir, path)
        Log.d("confirmCacheExists", targetFile.exists().toString())
        return targetFile.exists()
    }  // /Cache/?

    // Create FS Operator
    fun createFileDir(path: String){
        if (!confirmPathExists(path)){
            File(context.filesDir, path).mkdirs()
        }
    }  // /File
    fun createCacheDir(path: String){
        if (!confirmCacheExists(path)){
            File(context.cacheDir, path).mkdirs()
        }
    }  // /Cache/path  e.g. /Cache/Plugin
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

    // Re-Zip FS Operator
    fun rezipFromFile(originPluginFile: File, targetZipFile: File){
        targetZipFile.parentFile?.mkdirs()

        ZipOutputStream(FileOutputStream(targetZipFile)).use { zipOutputStream ->
            fun writeZipEntry(file: File, entryName: String) {
                if (file.canonicalFile == targetZipFile.canonicalFile) {
                    return
                }

                if (file.isDirectory) {
                    val directoryEntryName = entryName.trimEnd('/') + "/"
                    if (directoryEntryName != "/") {
                        zipOutputStream.putNextEntry(ZipEntry(directoryEntryName))
                        zipOutputStream.closeEntry()
                    }
                    file.listFiles()?.forEach { childFile ->
                        writeZipEntry(childFile, "$directoryEntryName${childFile.name}")
                    }
                } else {
                    zipOutputStream.putNextEntry(ZipEntry(entryName))
                    file.inputStream().use { inputStream ->
                        inputStream.copyTo(zipOutputStream)
                    }
                    zipOutputStream.closeEntry()
                }
            }

            if (originPluginFile.isDirectory) {
                originPluginFile.listFiles()?.forEach { childFile ->
                    writeZipEntry(childFile, childFile.name)
                }
            } else {
                writeZipEntry(originPluginFile, originPluginFile.name)
            }
        }
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
    fun readFileContent(filePath: String): String {
        return File(filePath).readText()
    }

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
    fun deleteFileOrDirectory(filePath: String): Boolean {
        val targetFileOrDirectory = File(filePath)
        return targetFileOrDirectory.deleteRecursively()
    }

    // Chmod FS Operator
    fun setPluginEntryPointExecutable(pluginManifest: PluginManifest): Boolean{
        val pluginRootDirectory = getInternalPluginRootDirectory()
        val pluginPackageName = pluginManifest.pluginPackageName
        val pluginEntryPoint = pluginManifest.entryPoint
        val _child = "$pluginPackageName/$pluginEntryPoint"
        return File(pluginRootDirectory,_child).setExecutable(true)
    }
    fun setEnvironmentEntryPointExecutable(environmentManifest: EnvironmentManifest): Boolean{
        val environmentRootDirectory = getInternalEnvironmentRootDirectory()
        val environmentPackageName = environmentManifest.environmentPackageName
        val environmentEntryPoint = environmentManifest.entryPoint
        val _child = "$environmentPackageName/$environmentEntryPoint"
        return File(environmentRootDirectory,_child).setExecutable(true)
    }

    // Share FS Operator
    fun getShareUriFromFile(file: File): Uri{
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

}
