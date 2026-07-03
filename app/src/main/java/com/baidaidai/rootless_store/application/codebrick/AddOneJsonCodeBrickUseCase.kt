package com.baidaidai.rootless_store.application.codebrick

import com.baidaidai.rootless_store.data.codebrick.gateway.CodeBrickGatewayImpl
import com.baidaidai.rootless_store.data.codebrick.repository.CodeBrickRepositoryImpl
import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import javax.inject.Inject

class AddOneJsonCodeBrickUseCase @Inject constructor(
    private val codeBrickGatewayImpl: CodeBrickGatewayImpl,
    private val codeBrickRepositoryImpl: CodeBrickRepositoryImpl
) {

    suspend operator fun invoke(): CodeBrickError? {

        // Get Clip Board
        val clipBoard = codeBrickGatewayImpl.getClipboardText() ?: return CodeBrickError(
            errorMessage = "Clipboard is empty.",
            errorCause = "CodeBrick json text is null."
        )

        // Convert from Json to CodeBrick Config
        val jsonCodeBrickConfig = codeBrickGatewayImpl.parseCodeBrickConfigFromJson(jsonString = clipBoard) ?: return CodeBrickError(
            errorMessage = "Invalid CodeBrick json.",
            errorCause = clipBoard
        )

        // Convert from JsonCodeBrickConfig to CodeBrickConfig
        val codeBrickConfig = CodeBrickConfig(
            unixTimeStamp = System.currentTimeMillis(),
            codeBrickTitle = jsonCodeBrickConfig.codeBrickTitle,
            codeBrickEnvironment = jsonCodeBrickConfig.codeBrickEnvironment,
            codeBrickContent = jsonCodeBrickConfig.codeBrickContent
        )


        // Add CodeBrick config
        codeBrickRepositoryImpl.createOneCodeBrickConfig(codeBrickConfig)

        return null
    }
}