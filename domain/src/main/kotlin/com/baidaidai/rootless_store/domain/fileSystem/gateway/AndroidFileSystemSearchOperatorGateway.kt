package com.baidaidai.rootless_store.domain.fileSystem.gateway

import java.io.File

interface AndroidFileSystemSearchOperatorGateway {

    fun hasPluginDirectory(): Boolean

    fun hasPluginCacheDirectory(): Boolean

    fun hasEnvironmentDirectory(): Boolean

    fun hasEnvironmentCacheDirectory(): Boolean

    fun findRegularFile(
        file: File
    ): File?

}
