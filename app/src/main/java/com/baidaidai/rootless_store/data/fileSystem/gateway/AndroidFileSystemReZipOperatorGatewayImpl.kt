package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class AndroidFileSystemReZipOperatorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Re-Zip FS Operator
    fun rezipFromFile(originPluginFile: File, targetZipFile: File) {
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

}
