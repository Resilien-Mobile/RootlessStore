package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.codebrick.gateway.CodeBrickGatewayImpl
import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickToken
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import javax.inject.Inject

class PostCodeBrickTokenUseCase @Inject constructor(
    private val codeBrickGatewayImpl: CodeBrickGatewayImpl
) {
    suspend operator fun invoke(
        codeBrickConfig: CodeBrickConfig
    ): Result<Unit, CodeBrickError> {

        // Map CodeBrickConfig to shareable CodeBrick payload
        val codeBrickJsonToken = CodeBrickToken(
            codeBrickTitle = codeBrickConfig.codeBrickTitle,
            codeBrickEnvironment = codeBrickConfig.codeBrickEnvironment,
            codeBrickContent = codeBrickConfig.codeBrickContent
        )

        // Create CodeBrick token
        val codeBrickToken = codeBrickGatewayImpl
            .createCodeBrickToken(codeBrickJsonToken)
            .getOrElse { codeBrickError ->
                return Err(codeBrickError)
            }

        // Post CodeBrick token to clipboard
        return codeBrickGatewayImpl.postClipboardText(codeBrickToken)
    }
}
