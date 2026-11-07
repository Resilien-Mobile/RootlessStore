package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.net.Uri
import java.io.File

interface AndroidFileSystemShareOperatorGateway {

    fun resolveShareUriFromFile(
        file: File
    ): Uri

}
