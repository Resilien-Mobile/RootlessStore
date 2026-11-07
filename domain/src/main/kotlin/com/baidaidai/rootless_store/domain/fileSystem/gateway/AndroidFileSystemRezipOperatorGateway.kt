package com.baidaidai.rootless_store.domain.fileSystem.gateway

import java.io.File

interface AndroidFileSystemRezipOperatorGateway {

    fun rezipFromFile(
        originPluginFile: File,
        targetZipFile: File
    )

}
