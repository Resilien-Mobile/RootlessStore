package com.baidaidai.rootless_store.application.assistant

import com.baidaidai.rootless_store.data.assistant.gateway.AssistantGatewayImpl
import com.baidaidai.rootless_store.domain.assistant.error.AssistantError
import com.baidaidai.rootless_store.domain.assistant.model.AssistantResponse
import com.github.michaelbull.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 将自然语言 prompt 解析为带合法性标记的 AssistantResponse（masked、非流式、一次性返回）。
 */
class ResolveMaskedResponseUseCase @Inject constructor(
    private val assistantGatewayImpl: AssistantGatewayImpl
) {

    suspend operator fun invoke(
        prompt: String
    ): Result<AssistantResponse, AssistantError> =
        withContext(Dispatchers.IO) {

            assistantGatewayImpl.resolveMaskedResponse(
                prompt = prompt
            )

        }

}
