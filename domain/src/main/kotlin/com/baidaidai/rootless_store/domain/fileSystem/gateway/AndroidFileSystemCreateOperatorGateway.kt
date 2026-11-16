package com.baidaidai.rootless_store.domain.fileSystem.gateway

import java.io.File

interface AndroidFileSystemCreateOperatorGateway {

    fun resolveChildFile(
        parentDirectory: File,
        childName: String
    ): File

    fun ensureDirectory(
        directory: File
    ): File?

}
