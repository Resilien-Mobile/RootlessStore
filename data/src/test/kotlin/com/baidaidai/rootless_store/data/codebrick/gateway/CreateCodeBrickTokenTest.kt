package com.baidaidai.rootless_store.data.codebrick.gateway

import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.gateway.CodeBrickDataSource
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickToken
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateCodeBrickTokenTest {

    private object FakeCodeBrickDataSource : CodeBrickDataSource {
        override fun findClipboardText(): Result<String, CodeBrickError> = Ok("")

        override fun postClipboardText(
            clipboardText: String
        ): Result<Unit, CodeBrickError> = Ok(Unit)
    }

    @Test
    fun createCodeBrickTokenNormal() {
        val gateway = CodeBrickGatewayImpl(FakeCodeBrickDataSource)

        val codeBrickToken = CodeBrickToken(
            codeBrickTitle = "Silent Mode",
            codeBrickEnvironment = ExecutionContext.ADB,
            codeBrickContent = "cmd audio set-ringer-mode SILENT"
        )

        val expectedToken = """
            {"codeBrickTitle":"Silent Mode","codeBrickEnvironment":"ADB","codeBrickContent":"cmd audio set-ringer-mode SILENT"}
        """.trimIndent()

        val result = gateway.createCodeBrickToken(codeBrickToken)

        assertEquals(expectedToken, result.getOrElse { error -> throw AssertionError(error) })
    }

}
