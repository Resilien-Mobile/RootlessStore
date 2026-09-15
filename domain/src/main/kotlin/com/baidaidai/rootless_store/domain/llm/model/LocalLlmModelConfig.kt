package com.baidaidai.rootless_store.domain.llm.model

data class LocalLlmModelConfig(
    val contextSize: Int = 2048,
    val threadCount: Int = 4
)
