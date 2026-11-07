package com.baidaidai.rootless_store.data.codebrick.gateway

import com.baidaidai.rootless_store.domain.codebrick.gateway.CodeBrickGateway
import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.gateway.CodeBrickDataSource
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickToken
import com.github.michaelbull.result.*
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CodeBrickGatewayImpl @Inject constructor(
    private val codeBrickDataSource: CodeBrickDataSource
): CodeBrickGateway {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override fun findClipboardText(): Result<String, CodeBrickError> {
        return codeBrickDataSource.findClipboardText()
    }

    override fun postClipboardText(
        clipboardText: String
    ): Result<Unit, CodeBrickError> {
        return codeBrickDataSource.postClipboardText(clipboardText)
    }

    override fun parseCodeBrickToken(
        jsonString: String
    ): Result<CodeBrickToken, CodeBrickError> = runCatching {
        json.decodeFromString<CodeBrickToken>(jsonString)
    }
        .mapError { throwable ->
            CodeBrickError(
                errorMessage = "Invalid CodeBrick json.",
                errorCause = throwable.stackTrace.formatAsMultilineString(),
                errorCompanion = "Rootless Store could not parse clipboard content as a CodeBrick JSON payload. Please check whether required fields and enum values match the CodeBrick schema."
            )
        }

    override fun createCodeBrickToken(
        codeBrickToken: CodeBrickToken
    ): Result<String, CodeBrickError> = runCatching {
        json.encodeToString(codeBrickToken)
    }
        .mapError { throwable ->
            CodeBrickError(
                errorMessage = "Can't create CodeBrick token.",
                errorCause = throwable.stackTrace.formatAsMultilineString(),
                errorCompanion = "Rootless Store could not serialize this CodeBrick into a shareable JSON token."
            )
        }

}
