package com.baidaidai.rootless_store.domain.codebrick.gateway

import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickToken
import com.github.michaelbull.result.Result

interface CodeBrickGateway {

    fun findClipboardText(): Result<String, CodeBrickError>

    fun postClipboardText(
        clipboardText: String
    ): Result<Unit, CodeBrickError>

    fun parseCodeBrickToken(
        jsonString: String
    ): Result<CodeBrickToken, CodeBrickError>

    fun createCodeBrickToken(
        codeBrickToken: CodeBrickToken
    ): Result<String, CodeBrickError>

}
