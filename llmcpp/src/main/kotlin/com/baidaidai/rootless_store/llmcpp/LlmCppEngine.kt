package com.baidaidai.rootless_store.llmcpp

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LlmCppEngine {
    private val llmCppNativeBridge = LlmCppNativeBridge()
    private var nativeHandle: Long = 0L

    suspend fun loadModel(
        modelPath: String,
        contextSize: Int,
        threadCount: Int
    ) {
        nativeHandle = llmCppNativeBridge.loadModel(
            modelPath = modelPath,
            contextSize = contextSize,
            threadCount = threadCount
        )
    }

    fun observeResponse(
        prompt: String,
        maxTokenCount: Int,
        temperature: Float
    ): Flow<String> {
        return flow {
            if (nativeHandle == 0L) return@flow

            llmCppNativeBridge
                .generate(
                    nativeHandle = nativeHandle,
                    prompt = prompt,
                    maxTokenCount = maxTokenCount,
                    temperature = temperature
                )
                .forEach { token ->
                    emit(token)
                    delay(16)
                }
        }
    }

    fun getResponse(
        prompt: String,
        maxTokenCount: Int,
        temperature: Float
    ): String {
        return llmCppNativeBridge
            .generate(
                nativeHandle = nativeHandle,
                prompt = prompt,
                maxTokenCount = maxTokenCount,
                temperature = temperature
            )
            .joinToString()
    }

    suspend fun unloadModel() {
        if (nativeHandle == 0L) return
        llmCppNativeBridge.unloadModel(nativeHandle)
        nativeHandle = 0L
    }
}
