package com.baidaidai.rootless_store.data.codebrick.gateway

import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.gateway.CodeBrickDataSource
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ParseCodeBrickTokenTest {

    private object FakeCodeBrickDataSource : CodeBrickDataSource {
        override fun findClipboardText(): Result<String, CodeBrickError> = Ok("")

        override fun postClipboardText(
            clipboardText: String
        ): Result<Unit, CodeBrickError> = Ok(Unit)
    }

    @Test
    fun parseCodeBrickTokenNormal() {

        val gateway = CodeBrickGatewayImpl(FakeCodeBrickDataSource)

        val jsonString = "{\"codeBrickTitle\":\"Silent Mode\",\"codeBrickEnvironment\":\"ADB\",\"codeBrickContent\":\"cmd audio set-ringer-mode SILENT\"}"

        val result = gateway.parseCodeBrickToken(jsonString)

        assertEquals(ExecutionContext.ADB, result.getOrElse { error -> throw AssertionError(error) }.codeBrickEnvironment)
    }

    @Test
    fun parseCodeBrickTokenFail() {
        val gateway = CodeBrickGatewayImpl(FakeCodeBrickDataSource)

        val jsonString = "\"codeBrickEnvironment\":\"ADB\",\"codeBrickContent\":\"cmd audio set-ringer-mode SILENT\"}"

        val result = gateway.parseCodeBrickToken(jsonString)

        assertFalse(result.isOk)
    }
}