package com.baidaidai.rootless_store.data.codebrick.gateway

import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.gateway.CodeBrickDataSource
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickJsonPayload
import com.github.michaelbull.result.*
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CodeBrickGatewayImpl @Inject constructor(
    private val codeBrickDataSource: CodeBrickDataSource
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun findClipboardText(): Result<String, CodeBrickError> {
        return codeBrickDataSource.findClipboardText()
    }

    fun parseCodeBrickJson(
        jsonString: String
    ): Result<CodeBrickJsonPayload, CodeBrickError> = runCatching {
        json.decodeFromString<CodeBrickJsonPayload>(jsonString)
    }
        .mapError { throwable ->
            CodeBrickError(
                errorMessage = "Invalid CodeBrick json.",
                errorCause = throwable.stackTrace.formatAsMultilineString(),
                errorCompanion = "Rootless Store could not parse clipboard content as a CodeBrick JSON payload. Please check whether required fields and enum values match the CodeBrick schema."
            )
        }

}
