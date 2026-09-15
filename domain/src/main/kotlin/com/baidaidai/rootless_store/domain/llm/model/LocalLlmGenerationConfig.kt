package com.baidaidai.rootless_store.domain.llm.model

data class LocalLlmGenerationConfig(
    val maxTokenCount: Int = 512,
    val temperature: Float = 0.8f
)
