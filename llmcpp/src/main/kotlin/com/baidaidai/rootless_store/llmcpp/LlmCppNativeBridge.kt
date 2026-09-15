package com.baidaidai.rootless_store.llmcpp

internal class LlmCppNativeBridge {
    external fun loadModel(
        modelPath: String,
        contextSize: Int,
        threadCount: Int
    ): Long

    external fun generate(
        nativeHandle: Long,
        prompt: String,
        maxTokenCount: Int,
        temperature: Float
    ): Array<String>

    external fun unloadModel(
        nativeHandle: Long
    )

    companion object {
        init {
            System.loadLibrary("rootless_store_llmcpp")
        }
    }
}
