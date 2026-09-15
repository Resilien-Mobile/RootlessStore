package com.baidaidai.rootless_store.application.llmcpp

import com.baidaidai.rootless_store.data.llmcpp.gateway.LocalLlmGatewayImpl
import com.baidaidai.rootless_store.domain.llm.model.LocalLlmGenerationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLocalModelResponseUseCase @Inject constructor(
    private val localLlmGatewayImpl: LocalLlmGatewayImpl
) {

    suspend operator fun invoke(userInput: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            if (!localLlmGatewayImpl.hasModelFile()) {
                error("No Model Found")
            }

            val prompt = """
                你是一个资深的 Linux 工程师，现在用户提出了一个问题：${userInput}。
                你需要结合 Linux 已有的能力和 Android 适配性，生成一个符合 DTO 的解决方案：
                {
                    "context": "{{value}}"
                }
            """.trimIndent()

            localLlmGatewayImpl.getLocalLlmResponse(
                prompt = prompt,
                localLlmGenerationConfig = LocalLlmGenerationConfig()
            )
        }
    }
}
