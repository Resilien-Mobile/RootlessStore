package com.baidaidai.rootless_store.data.codebrick.gateway

import com.baidaidai.rootless_store.domain.codebrick.error.CodeBrickError
import com.baidaidai.rootless_store.domain.codebrick.gateway.CodeBrickDataSource
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickJsonPayload
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import org.junit.Assert.assertEquals
import org.junit.Test

class CodeBrickGatewayImplTest {

    // JSON
    @Test
    fun testNormalParseCodeBrickJson() {
        val fakeCodeBrickDataSource = object : CodeBrickDataSource {
            override fun findClipboardText(): Result<String, CodeBrickError> {
                return Ok( "{\"codeBrickTitle\":\"Silent Mode\",\"codeBrickEnvironment\":\"ADB\",\"codeBrickContent\":\"cmd audio set-ringer-mode SILENT\"}")
            }
        }

        val codeBrickGateway = CodeBrickGatewayImpl(fakeCodeBrickDataSource)

        val expectCodeBrickJsonPayload = CodeBrickJsonPayload(
            codeBrickTitle = "Silent Mode",
            codeBrickEnvironment = ExecutionContext.ADB,
            codeBrickContent = "cmd audio set-ringer-mode SILENT"
        )

        val jsonString: String = fakeCodeBrickDataSource
            .findClipboardText()
            .getOrElse {
                throw AssertionError("Unexpected error: $it")
            }
        val codeBrickJsonPayload = codeBrickGateway
            .parseCodeBrickJson(jsonString)
            .getOrElse {
                throw AssertionError("Unexpected error: $it")
            }

        assertEquals(expectCodeBrickJsonPayload, codeBrickJsonPayload)
    }

    @Test
    fun testMissingParameterParseCodeBrickJson() {
        val fakeCodeBrickDataSource = object : CodeBrickDataSource {
            override fun findClipboardText(): Result<String, CodeBrickError> {
                return Ok("{\"codeBrickTitle\":\"Silent Mode\",\"codeBrickEnvironment\":\"ADB\"}")
            }
        }

        val codeBrickGateway = CodeBrickGatewayImpl(fakeCodeBrickDataSource)

        val jsonString: String = fakeCodeBrickDataSource
            .findClipboardText()
            .getOrElse {
                throw AssertionError("Unexpected error: $it")
            }

        val codeBrickJsonPayload = codeBrickGateway.parseCodeBrickJson(jsonString)

        assertEquals(false,codeBrickJsonPayload.isOk)
    }

    @Test
    fun testErrorEnvironmentParseCodeBrickJson() {
        val fakeCodeBrickDataSource = object : CodeBrickDataSource {
            override fun findClipboardText(): Result<String, CodeBrickError> {
                return Ok("{\"codeBrickTitle\":\"Silent Mode\",\"codeBrickEnvironment\":\"UNKNOW\",\"codeBrickContent\":\"cmd audio set-ringer-mode SILENT\"}")
            }
        }

        val codeBrickGateway = CodeBrickGatewayImpl(fakeCodeBrickDataSource)

        val jsonString: String = fakeCodeBrickDataSource
            .findClipboardText()
            .getOrElse {
                throw AssertionError("Unexpected error: $it")
            }

        val codeBrickJsonPayload = codeBrickGateway.parseCodeBrickJson(jsonString)

        assertEquals(false,codeBrickJsonPayload.isOk)
    }

    // ClipBoard
    @Test
    fun findClipboardTextSuccessCase() {
        val fakeCodeBrickDataSource = object : CodeBrickDataSource {
            override fun findClipboardText(): Result<String, CodeBrickError> {
                return Ok("fake clipboard text")
            }
        }

        val codeBrickGateway = CodeBrickGatewayImpl(fakeCodeBrickDataSource)

        val actualText = codeBrickGateway
            .findClipboardText()

        assertEquals(true, actualText.isOk)
    }

    @Test
    fun findClipboardTextErrorCase() {
        val fakeCodeBrickDataSource = object : CodeBrickDataSource {
            override fun findClipboardText(): Result<String, CodeBrickError> {
                return Err(CodeBrickError("",""))
            }
        }

        val codeBrickGateway = CodeBrickGatewayImpl(fakeCodeBrickDataSource)

        val actualText = codeBrickGateway
            .findClipboardText()

        assertEquals(false, actualText.isOk)
    }

}
