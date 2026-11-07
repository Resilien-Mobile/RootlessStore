package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.net.Uri
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import java.io.File

interface AndroidFileSystemReadOperatorGateway {

    fun loadBytesFromUri(
        uri: Uri
    ): ByteArray?

    fun hasFileInZip(
        uri: Uri,
        fileName: String
    ): Boolean

    fun hasFileInZip(
        file: File,
        fileName: String
    ): Boolean

    fun loadBytesInZip(
        file: File,
        fileName: String
    ): ByteArray?

    fun loadRawMagiskModuleProp(
        uri: Uri
    ): String

    fun loadRawMagiskModuleProp(
        file: File
    ): String

    fun loadRawPluginManifest(
        uri: Uri
    ): String

    fun loadRawPluginManifest(
        file: File
    ): String

    fun loadRawEnvironmentManifest(
        uri: Uri
    ): String

    fun loadRawEnvironmentManifest(
        file: File
    ): String

    fun loadFileContent(
        filePath: String
    ): String

    fun parsePluginManifest(
        jsonContent: String
    ): PluginManifest

    fun parseEnvironmentManifest(
        jsonContent: String
    ): EnvironmentManifest

}
