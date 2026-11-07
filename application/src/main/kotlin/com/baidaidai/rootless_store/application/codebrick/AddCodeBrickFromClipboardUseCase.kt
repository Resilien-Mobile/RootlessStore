package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.domain.codebrick.gateway.CodeBrickGateway
import com.baidaidai.rootless_store.domain.codebrick.repository.CodeBrickRepository
import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import javax.inject.Inject

class AddCodeBrickFromClipboardUseCase @Inject constructor(
    private val codeBrickGateway: CodeBrickGateway,
    private val codeBrickRepository: CodeBrickRepository
) {

    suspend operator fun invoke(): Result<Unit, CodeBrickError> {

        // Find Clipboard Content
        val clipboardText = codeBrickGateway
            .findClipboardText()
            .getOrElse { codeBrickError ->
                return Err(codeBrickError)
            }

        // Parse CodeBrick JSON
        val codeBrickJsonPayload = codeBrickGateway
            .parseCodeBrickToken(jsonString = clipboardText)
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
        codeBrickRepository.addCodeBrick(codeBrickConfig)

        return Ok(Unit)

    }
}
