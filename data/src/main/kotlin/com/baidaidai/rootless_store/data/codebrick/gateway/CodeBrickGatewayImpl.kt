package com.baidaidai.rootless_store.data.codebrick.gateway

import android.content.ClipboardManager
import android.content.Context
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickJsonPayload
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CodeBrickGatewayImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun findClipboardText(): String? {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboardManager.primaryClip ?: return null
        val clipItem = clipData.getItemAt(0) ?: return null
        return clipItem.coerceToText(context)?.toString()
    }

    fun parseCodeBrickJson(
        jsonString: String
    ): CodeBrickJsonPayload?{
        return runCatching {
            json.decodeFromString<CodeBrickJsonPayload>(jsonString)
        }.getOrNull()
    }

}
