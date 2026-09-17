package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.codebrick.gateway.CodeBrickGatewayImpl
import com.baidaidai.rootless_store.data.codebrick.repository.CodeBrickRepositoryImpl
import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import javax.inject.Inject

class AddCodeBrickFromClipboardUseCase @Inject constructor(
    private val codeBrickGatewayImpl: CodeBrickGatewayImpl,
    private val codeBrickRepositoryImpl: CodeBrickRepositoryImpl
) {

    suspend operator fun invoke(): Result<Unit, CodeBrickError> {

        // Find Clipboard Content
        val clipboardText = codeBrickGatewayImpl
            .findClipboardText()
            .getOrElse { codeBrickError ->
                return Err(codeBrickError)
            }

        // Parse CodeBrick JSON
        val codeBrickJsonPayload = codeBrickGatewayImpl
            .parseCodeBrickJson(jsonString = clipboardText)
            .getOrElse { codeBrickError ->
                return Err(codeBrickError)
            }

        // Create CodeBrick Config
        val codeBrickConfig = CodeBrickConfig(
            unixTimestamp = System.currentTimeMillis(),
            codeBrickTitle = codeBrickJsonPayload.codeBrickTitle,
            codeBrickEnvironment = codeBrickJsonPayload.codeBrickEnvironment,
            codeBrickContent = codeBrickJsonPayload.codeBrickContent
        )

        // Add CodeBrick Config
        codeBrickRepositoryImpl.addCodeBrick(codeBrickConfig)

        return Ok(Unit)

    }
}
