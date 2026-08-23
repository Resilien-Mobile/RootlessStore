package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.codebrick.gateway.CodeBrickGatewayImpl
import com.baidaidai.rootless_store.data.codebrick.repository.CodeBrickRepositoryImpl
import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import javax.inject.Inject

class AddCodeBrickFromClipboardUseCase @Inject constructor(
    private val codeBrickGatewayImpl: CodeBrickGatewayImpl,
    private val codeBrickRepositoryImpl: CodeBrickRepositoryImpl
) {

    suspend operator fun invoke(): CodeBrickError? {

        // Find Clipboard Content
        val clipboardText = codeBrickGatewayImpl.findClipboardText() ?: return CodeBrickError(
            errorMessage = "Clipboard is empty.",
            errorCause = "CodeBrick json text is null."
        )

        // Parse CodeBrick JSON
        val codeBrickJsonPayload = codeBrickGatewayImpl.parseCodeBrickJson(jsonString = clipboardText) ?: return CodeBrickError(
            errorMessage = "Invalid CodeBrick json.",
            errorCause = clipboardText
        )

        // Create CodeBrick Config
        val codeBrickConfig = CodeBrickConfig(
            unixTimestamp = System.currentTimeMillis(),
            codeBrickTitle = codeBrickJsonPayload.codeBrickTitle,
            codeBrickEnvironment = codeBrickJsonPayload.codeBrickEnvironment,
            codeBrickContent = codeBrickJsonPayload.codeBrickContent
        )

        // Add CodeBrick Config
        codeBrickRepositoryImpl.createCodeBrickConfig(codeBrickConfig)

        return null
    }
}
