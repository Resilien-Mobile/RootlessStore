package com.baidaidai.rootless_store.application.assistant

import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemCreateOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemDefaultOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemSearchOperatorGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FindAssistantModelFileUseCase @Inject constructor(
    private val androidFileSystemDefaultOperatorGateway: AndroidFileSystemDefaultOperatorGateway,
    private val androidFileSystemCreateOperatorGateway: AndroidFileSystemCreateOperatorGateway,
    private val androidFileSystemSearchOperatorGateway: AndroidFileSystemSearchOperatorGateway
) {

    suspend operator fun invoke(): File? = withContext(Dispatchers.IO) {
        val externalFilesDirectory = androidFileSystemDefaultOperatorGateway
            .findExternalAppFilesDirectoryFile()
            ?: return@withContext null

        val modelDirectory = androidFileSystemCreateOperatorGateway.resolveChildFile(
            parentDirectory = externalFilesDirectory,
            childName = "models"
        )
        val modelFile = androidFileSystemCreateOperatorGateway.resolveChildFile(
            parentDirectory = modelDirectory,
            childName = "Qwen.litertlm"
        )

        androidFileSystemSearchOperatorGateway.findRegularFile(modelFile)
    }

}
