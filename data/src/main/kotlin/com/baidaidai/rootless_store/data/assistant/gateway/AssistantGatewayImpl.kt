package com.baidaidai.rootless_store.data.assistant.gateway

import com.baidaidai.rootless_store.data.assistant.model.AssistantApi
import com.baidaidai.rootless_store.data.assistant.mapper.LlmMapper.toShellCommand
import com.baidaidai.rootless_store.domain.assistant.error.AssistantError
import com.baidaidai.rootless_store.domain.assistant.model.AssistantResponse
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssistantGatewayImpl @Inject constructor(
    private val assistantApi: AssistantApi
) {

    @Synchronized
    fun resolveUnmaskedResponse(
        prompt: String
    ): Result<String, AssistantError> = assistantApi.resolveUnmaskedResponse(
        prompt = prompt
    )

    @Synchronized
    fun resolveMaskedResponse(
        prompt: String
    ): Result<AssistantResponse, AssistantError> = assistantApi
        .resolveMaskedResponse(prompt = prompt)
        .map { shellCommandDto ->
            shellCommandDto.toShellCommand()
        }



}
