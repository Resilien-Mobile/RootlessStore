package com.baidaidai.rootless_store.domain.llm.model

data class LocalLlmModel(
    val modelName: String,
    val modelPath: String,
    val modelSize: Long
)
