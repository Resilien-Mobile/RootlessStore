package com.baidaidai.rootless_store.application.llmcpp

import com.baidaidai.rootless_store.data.llmcpp.gateway.LocalLlmGatewayImpl
import com.baidaidai.rootless_store.domain.llm.error.LlmError
import com.baidaidai.rootless_store.domain.llm.model.LocalLlmModelConfig
import javax.inject.Inject

class LoadLocalModelUseCase @Inject constructor(
    private val localLlmGatewayImpl: LocalLlmGatewayImpl
) {

    suspend operator fun invoke(): LlmError? {

        // resolve fixed model path
        val modelPath = localLlmGatewayImpl.getLocalModelPath()

        if (!localLlmGatewayImpl.hasModelFile()) {
            return LlmError(
                errorMessage = "Local LLM model not found",
                errorCause = "Missing model file: $modelPath"
            )
        }

        // build model config
        val modelConfig = LocalLlmModelConfig(
            contextSize = 4096,
            threadCount = 4
        )

        // load model
        localLlmGatewayImpl.loadLocalLlmModel(
            modelPath = modelPath,
            localLlmModelConfig = modelConfig
        )

        return null
    }
}
