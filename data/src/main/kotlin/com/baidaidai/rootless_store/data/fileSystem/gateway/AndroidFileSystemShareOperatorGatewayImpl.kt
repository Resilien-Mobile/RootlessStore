package com.baidaidai.rootless_store.data.fileSystem.gateway

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AndroidFileSystemShareOperatorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Share FS Operator
    fun resolveShareUriFromFile(file: File): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

}
