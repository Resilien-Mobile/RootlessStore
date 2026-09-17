package com.baidaidai.rootless_store.data.codebrick.datasource

import android.content.ClipboardManager
import android.content.Context
import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.gateway.CodeBrickDataSource
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CodeBrickDataSourceImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : CodeBrickDataSource {

    override fun findClipboardText(): Result<String, CodeBrickError> {

        val clipData = clipboardManager().primaryClip ?: return Err(
            CodeBrickError(
                errorCause = "ClipboardManager.primaryClip is null.",
                errorMessage = "Clipboard is empty.",
                errorCompanion = "Rootless Store could not find any clipboard content to parse as CodeBrick JSON."
            )
        )
        val clipItem = clipData.getItemAt(0) ?: return Err(
            CodeBrickError(
                errorCause = "ClipData.getItemAt(0) returned null.",
                errorMessage = "Clipboard item is empty.",
                errorCompanion = "Rootless Store found clipboard data, but the first clipboard item is not available."
            )
        )

        return Ok(clipItem.coerceToText(context).toString())
    }

    private fun clipboardManager(): ClipboardManager {
        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
}
