package com.baidaidai.rootless_store.domain.fileSystem.gateway

interface AndroidFileSystemDeleteOperatorGateway {

    fun deleteFile(
        pluginPackageName: String
    ): Boolean

    fun deleteDirectoryByPackageName(
        pluginPackageName: String
    ): Boolean

    fun deleteEnvironmentDirectoryByPackageName(
        environmentPackageName: String
    ): Boolean

    fun deleteFileOrDirectory(
        filePath: String
    ): Boolean

}
