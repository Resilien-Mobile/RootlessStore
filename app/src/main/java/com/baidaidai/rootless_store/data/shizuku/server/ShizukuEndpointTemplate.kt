package com.baidaidai.rootless_store.data.shizuku.server

import IShellCallback
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

internal class ShizukuEndpointTemplate : IShellService.Stub() {

    private var currentDirectory: String = "/sdcard"

    override fun exec(
        pluginDirectory: String,
        pluginEntryPoint: String,
        enableMonitor: Boolean,
        callback: IShellCallback,
    ) {

        val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        coroutineScope.launch {
            val processBuilder = ProcessBuilder("sh","-c","echo PID:$$;cd /data/user_de/0/com.android.shell/RootlessStore/Plugin/$pluginDirectory && ./$pluginEntryPoint")

            val process = processBuilder.start()

            process
                .inputStream
                .bufferedReader()
                .useLines{ line ->
                    line.forEach {
                        callback.onExecute(it)
                    }
                }

            process
                .errorStream
                .bufferedReader()
                .useLines{ line ->
                    line.forEach {
                        callback.onError(it)
                    }
                }

            if (enableMonitor){
                coroutineScope.launch {
                    val exitCode = process.waitFor()
                    callback.onProcessExited(exitCode)
                }
            }
        }

    }

    override fun kill(progressPid: Int): Boolean {

        val process = ProcessBuilder(
            "sh","-c","kill -9 $progressPid"
        )
            .redirectErrorStream(true)
            .start()
            .waitFor()
        return process == 0
    }

    override fun command(
        commandContent: String,
        callback: IShellCallback,
        jumpToDirectory: Boolean
    ) {

        var commandContent = commandContent

        if(jumpToDirectory){
            changeDirectoryHandler("/data/user_de/0/com.android.shell/RootlessStore/Plugin")
        }
        if(commandContent.startsWith("cd ")){
            val targetDirectory = commandContent.removePrefix("cd ").trim()
            changeDirectoryHandler(targetDirectory)
            commandContent = "exit"
        }

        val processBuilder = ProcessBuilder("sh","-c","${changeDirectoryHandler()}$commandContent")
        val process = processBuilder.start()

        process
            .inputStream
            .bufferedReader()
            .useLines{ line ->
                line.forEach {
                    callback.onExecute(it)
                }
            }

        process
            .errorStream
            .bufferedReader()
            .useLines{ line ->
                line.forEach {
                    callback.onError(it)
                }
            }
    }

    override fun installShellPlugin(
        shellPluginStagingFilePath: String,
        pluginPackageName: String,
        entryPoint: String
    ): Boolean {
        // Validate shell plugin install arguments
        if (shellPluginStagingFilePath.isBlank()) {
            return false
        }
        if (!pluginPackageName.isSafeFileName()) {
            return false
        }
        if (!entryPoint.isSafeRelativePath()) {
            return false
        }

        // Get shell plugin staging zip
        val pluginZipName = "_template_.zip"
        val shellPluginZipFile = File(shellPluginStagingFilePath)

        if (shellPluginZipFile.name != pluginZipName) {
            return false
        }

        if (!shellPluginZipFile.isFile) {
            return false
        }

        // Get shell private plugin package directory
        val shellPrivateRootDirectory = File("/data/user_de/0/com.android.shell")
        val shellPluginRootDirectory = File(shellPrivateRootDirectory, "RootlessStore/Plugin")
        val shellPluginPackageDirectory = File(shellPluginRootDirectory, pluginPackageName)

        return try {
            // Create shell plugin package directory
            shellPluginPackageDirectory.mkdirs()

            // Unzip shell plugin zip stream to package directory
            unzipShellPluginPackage(
                shellPluginZipFile = shellPluginZipFile,
                shellPluginPackageDirectory = shellPluginPackageDirectory
            )

            // Set shell plugin entry point executable
            val shellPluginEntryPointFile = shellPluginPackageDirectory.safeResolveRelativeFile(entryPoint)
                ?: return false

            if (!shellPluginEntryPointFile.isFile) {
                return false
            }

            shellPluginEntryPointFile.setReadable(true, true)
            shellPluginEntryPointFile.setExecutable(true, true)
        } catch (throwable: Throwable) {
            Log.e("ShizukuEndpointTemplate", "installShellPlugin failed", throwable)
            shellPluginPackageDirectory.deleteRecursively()
            false
        }
    }

    override fun uninstallShellPlugin(
        pluginPackageName: String
    ): Boolean {
        // Get shell private plugin package directory
        val shellPrivateRootDirectory = File("/data/user_de/0/com.android.shell")
        val shellPluginRootDirectory = File(shellPrivateRootDirectory, "RootlessStore/Plugin")
        val shellPluginPackageDirectory = File(shellPluginRootDirectory, pluginPackageName)

        // Delete shell plugin package directory
        return shellPluginPackageDirectory.deleteRecursively()
    }

    override fun exportShellPlugin(
        pluginPackageName: String,
        shellPluginExportZipPath: String
    ): Boolean {
        // Validate shell plugin export arguments
        if (!pluginPackageName.isSafeFileName()) {
            return false
        }
        if (shellPluginExportZipPath.isBlank()) {
            return false
        }

        // Get shell private plugin package directory
        val shellPrivateRootDirectory = File("/data/user_de/0/com.android.shell")
        val shellPluginRootDirectory = File(shellPrivateRootDirectory, "RootlessStore/Plugin")
        val shellPluginPackageDirectory = File(shellPluginRootDirectory, pluginPackageName)

        if (!shellPluginPackageDirectory.isDirectory) {
            return false
        }

        // Get shell plugin export zip
        val shellPluginExportZipFile = File(shellPluginExportZipPath)

        return try {
            // Re-Zip shell plugin package directory to app external cache
            rezipShellPluginPackage(
                shellPluginPackageDirectory = shellPluginPackageDirectory,
                shellPluginExportZipFile = shellPluginExportZipFile
            )

            shellPluginExportZipFile.isFile
        } catch (throwable: Throwable) {
            Log.e("ShizukuEndpointTemplate", "exportShellPlugin failed", throwable)
            false
        }
    }

    private fun buildEnvironmentConfigExportString(
        environmentConfigKeyList: List<String>,
        environmentConfigValueList: List<String>
    ): String {
        return environmentConfigKeyList
            .zip(environmentConfigValueList)
            .filter { (key, _) -> key.isValidShellVariableName() }
            .joinToString(separator = "; ") { (key, value) ->
                "export $key=${value.shellQuote()}"
            }
    }

    private fun String.isValidShellVariableName(): Boolean {
        return matches(Regex("[A-Za-z_][A-Za-z0-9_]*"))
    }

    private fun String.shellQuote(): String {
        return "'${replace("'", "'\"'\"'")}'"
    }

    private fun String.isSafeFileName(): Boolean {
        return isNotBlank() &&
            !contains("/") &&
            !contains("\\") &&
            !contains("..")
    }

    private fun String.isSafeRelativePath(): Boolean {
        return isNotBlank() &&
            !startsWith("/") &&
            !contains("\\") &&
            split("/").none { pathSegment ->
                pathSegment.isBlank() || pathSegment == "." || pathSegment == ".."
            }
    }

    private fun File.safeResolveRelativeFile(relativePath: String): File? {
        val rootCanonicalFile = canonicalFile
        val targetFile = File(this, relativePath).canonicalFile

        return when {
            targetFile.path == rootCanonicalFile.path -> {
                targetFile
            }
            targetFile.path.startsWith(rootCanonicalFile.path + File.separator) -> {
                targetFile
            }
            else -> {
                null
            }
        }
    }

    private fun unzipShellPluginPackage(
        shellPluginZipFile: File,
        shellPluginPackageDirectory: File
    ) {
        FileInputStream(shellPluginZipFile).use { fileInputStream ->
            ZipInputStream(BufferedInputStream(fileInputStream)).use { zipInputStream ->
                var zipEntry = zipInputStream.nextEntry

                while (zipEntry != null) {
                    val outputFile = shellPluginPackageDirectory.safeResolveRelativeFile(zipEntry.name)
                        ?: throw IllegalArgumentException("Unsafe zip entry: ${zipEntry.name}")

                    if (zipEntry.isDirectory) {
                        outputFile.mkdirs()
                    } else {
                        outputFile.parentFile?.mkdirs()
                        FileOutputStream(outputFile).use { fileOutputStream ->
                            zipInputStream.copyTo(fileOutputStream)
                        }
                    }

                    zipInputStream.closeEntry()
                    zipEntry = zipInputStream.nextEntry
                }
            }
        }
    }

    private fun rezipShellPluginPackage(
        shellPluginPackageDirectory: File,
        shellPluginExportZipFile: File
    ) {
        shellPluginExportZipFile.parentFile?.mkdirs()

        ZipOutputStream(FileOutputStream(shellPluginExportZipFile)).use { zipOutputStream ->
            fun writeZipEntry(file: File, entryName: String) {
                if (file.canonicalFile == shellPluginExportZipFile.canonicalFile) {
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

            shellPluginPackageDirectory.listFiles()?.forEach { childFile ->
                writeZipEntry(childFile, childFile.name)
            }
        }
    }

    private fun changeDirectoryHandler(directory: String = currentDirectory): String{

        currentDirectory = when {
            directory.startsWith("/") -> {
                File(directory).canonicalPath
            }
            else -> {
                File(currentDirectory, directory).canonicalPath
            }
        }

        return "cd $currentDirectory &&"

    }

}
