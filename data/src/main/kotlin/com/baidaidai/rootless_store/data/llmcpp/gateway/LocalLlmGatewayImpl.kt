package com.baidaidai.rootless_store.data.llmcpp.gateway

import android.content.Context
import com.baidaidai.rootless_store.domain.llm.model.LocalLlmGenerationConfig
import com.baidaidai.rootless_store.domain.llm.model.LocalLlmModelConfig
import com.baidaidai.rootless_store.llmcpp.LlmCppEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class LocalLlmGatewayImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val llmCppEngine = LlmCppEngine()

    // Runtime
    suspend fun loadLocalLlmModel(
        modelPath: String,
        localLlmModelConfig: LocalLlmModelConfig
    ) {
        llmCppEngine.loadModel(
            modelPath = modelPath,
            contextSize = localLlmModelConfig.contextSize,
            threadCount = localLlmModelConfig.threadCount
        )
    }

    fun getLocalLlmResponse(
        prompt: String,
        localLlmGenerationConfig: LocalLlmGenerationConfig
    ): String {
        return llmCppEngine
            .getResponse(
                prompt = prompt,
                maxTokenCount = localLlmGenerationConfig.maxTokenCount,
                temperature = localLlmGenerationConfig.temperature
            )
    }

//    suspend fun unloadLocalLlmModel() {
//        llmCppEngine.unloadModel()
//    }

    // Read
    fun getLocalModelPath(): String {
        return File(context.filesDir, "LLM")
            .resolve("llm.gguf")
            .absolutePath
    }

    fun hasModelFile(): Boolean {
        return File(getLocalModelPath()).exists()
    }

//    private fun findDisplayName(uri: Uri): String {
//        val displayName = context.contentResolver
//            .query(uri, null, null, null, null)
//            ?.use { cursor ->
//                val displayNameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                if (cursor.moveToFirst() && displayNameColumnIndex >= 0) {
//                    cursor.getString(displayNameColumnIndex)
//                } else {
//                    null
//                }
//            }
//
//        return displayName ?: "local_llm_model.gguf"
//    }
}
