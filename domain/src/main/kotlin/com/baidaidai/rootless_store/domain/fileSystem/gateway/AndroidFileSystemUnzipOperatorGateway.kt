package com.baidaidai.rootless_store.domain.fileSystem.gateway

import io.ktor.utils.io.ByteReadChannel
import java.io.File

interface AndroidFileSystemUnzipOperatorGateway {

    fun unzipFromFileToDirectory(
        originFile: File,
        targetDirectory: File
    )

    fun extractFilesFromZip(
        originFile: File,
        targetDirectory: File,
        fileNames: Set<String>
    )

    fun unzipFromByteChannelToDirectory(
        originFileByteChannel: ByteReadChannel,
        targetDirectory: File
    )

    fun unzipEnvironmentFromFile(
        originFile: File,
        pluginRootDirectory: File,
        directoryName: String
    )

    fun unzipFromUri(
        originFileByteChannel: ByteReadChannel,
        pluginRootDirectory: File,
        directoryName: String
    )

    fun unzipEnvironmentFromUri(
        originFileByteChannel: ByteReadChannel,
        pluginRootDirectory: File,
        directoryName: String
    )

}
