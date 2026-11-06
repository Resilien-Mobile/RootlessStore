package com.baidaidai.rootless_store.data.update.gateway

import com.baidaidai.rootless_store.data.update.remote.api.GithubReleaseApi
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class FetchLatestVersionTagTest {

    @Before
    fun setup() {

        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0

    }

    @After
    fun tearDown() {

        unmockkStatic(Log::class)

    }

    @Test
    fun fetchLatestVersionTagNormal() {

        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"tag_name":"v2.3.1"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(
                    name = HttpHeaders.ContentType,
                    value = ContentType.Application.Json.toString(),
                ),
            )
        }
        val actualResult = runBlocking(
            block = {
                fetchLatestVersionTag(
                    mockEngine = mockEngine,
                )
            },
        )

        assertTrue(actualResult.isSuccess)
        assertEquals("v2.3.1", actualResult.getOrNull())

    }

    @Test
    fun fetchLatestVersionTagNetworkFail() {

        val mockEngine = MockEngine { _ ->
            throw IOException("Network is unavailable")
        }
        val actualResult = runBlocking(
            block = {
                fetchLatestVersionTag(
                    mockEngine = mockEngine,
                )
            },
        )

        assertTrue(actualResult.isFailure)

    }

    @Test
    fun fetchLatestVersionTagInvalidResponseFail() {

        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"name":"Release without a tag"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(
                    name = HttpHeaders.ContentType,
                    value = ContentType.Application.Json.toString(),
                ),
            )
        }
        val actualResult = runBlocking(
            block = {
                fetchLatestVersionTag(
                    mockEngine = mockEngine,
                )
            },
        )

        assertTrue(actualResult.isFailure)

    }

    private suspend fun fetchLatestVersionTag(
        mockEngine: MockEngine,
    ): Result<String> {

        val ktorClient = HttpClient(
            engine = mockEngine,
        ) {
            install(
                plugin = ContentNegotiation,
            ) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    },
                )
            }
        }

        return try {
            val githubReleaseApi = GithubReleaseApi(
                ktorClient = ktorClient,
            )
            val storeUpdateGatewayImpl = StoreUpdateGatewayImpl(
                githubReleaseApi = githubReleaseApi,
            )

            storeUpdateGatewayImpl.fetchLatestVersionTag()
        } finally {
            ktorClient.close()
        }

    }

}
