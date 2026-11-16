package com.baidaidai.rootless_store.data.assistant.model

import com.baidaidai.rootless_store.data.assistant.api.dto.AssistantDto
import com.baidaidai.rootless_store.domain.assistant.error.AssistantError
import com.github.michaelbull.result.Result

interface AssistantApi {

    fun resolveUnmaskedResponse(
        prompt: String
    ): Result<String, AssistantError>

    fun resolveMaskedResponse(
        prompt: String
    ): Result<AssistantDto, AssistantError>

}
