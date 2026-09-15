package com.baidaidai.rootless_store.application.llmcpp

import android.content.Context
import android.net.Uri
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDeleteOperatorGatewayImpl
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class InstallLocalModelUseCase @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val androidFileSystemCreateOperatorGatewayImpl: AndroidFileSystemCreateOperatorGatewayImpl,
    private val androidFileSystemDeleteOperatorGatewayImpl: AndroidFileSystemDeleteOperatorGatewayImpl
) {

    suspend operator fun invoke(uri: Uri) = withContext(Dispatchers.IO) {

        // ensure LLM directory
        androidFileSystemCreateOperatorGatewayImpl.ensureFilesDirectory("LLM")

        // resolve LLM directory
        val llmDirectory = File(context.filesDir, "LLM")

        // resolve model file
        val modelFile = androidFileSystemCreateOperatorGatewayImpl.resolveChildFile(llmDirectory, "llm.gguf")

        // delete existed model file (force rename)
        androidFileSystemDeleteOperatorGatewayImpl.deleteFileOrDirectory(modelFile.path)

        // copy model into internal storage
        androidFileSystemCreateOperatorGatewayImpl.copyUriToFile(uri, modelFile)
    }
}
