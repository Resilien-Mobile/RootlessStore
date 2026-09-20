package com.baidaidai.rootless_store.domain.codebrick.gateway

import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.github.michaelbull.result.Result

interface CodeBrickDataSource {
    fun findClipboardText(): Result<String, CodeBrickError>

    fun postClipboardText(
        clipboardText: String
    ): Result<Unit, CodeBrickError>
}
