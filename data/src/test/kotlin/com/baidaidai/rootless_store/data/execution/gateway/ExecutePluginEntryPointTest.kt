package com.baidaidai.rootless_store.data.execution.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResultTag
import com.topjohnwu.superuser.Shell
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExecutePluginEntryPointTest {

    private val environmentRepository = mockk<EnvironmentRepositoryImpl>()
    private val pluginExecutionGateway = PluginExecutionGatewayImpl(
        shizukuUserServiceGatewayImpl = mockk(),
        environmentRepositoryImpl = environmentRepository,
        pluginProcessMonitor = mockk(),
    )

    @Before
    fun setup() {

        val localShell = mockk<Shell>()
        mockkStatic(
            Shell::class,
            Log::class,
        )
        every { Shell.getShell() } returns localShell
        every { localShell.isRoot } returns false
        every { Log.d(any(), any()) } returns 0
        coEvery { environmentRepository.resolveEnvironmentRuntimePath() } returns ""
        coEvery { environmentRepository.resolveEnvironmentLdPath() } returns ""
        coEvery { environmentRepository.resolveEnvironmentConfig() } returns emptyMap()

    }

    @After
    fun tearDown() {

        unmockkStatic(
            Shell::class,
            Log::class,
        )

    }

    @Test
    fun executePluginEntryPointNormal() = runBlocking {

        val actualExecutionResults = withTimeout(
            timeMillis = 5_000,
        ) {
            pluginExecutionGateway.executePluginEntryPoint(
                pluginEntryPoint = "printf hello",
                pluginPackageDirectory = ".",
            ).take(
                count = 2,
            ).toList()
        }

        val expectedExecutionResult = ExecutionResult(
            resultTag = ExecutionResultTag.Normal,
            output = "- hello",
        )

        val actualProcessIdOutput = actualExecutionResults[0].output.startsWith(
            prefix = "- PID:",
        )

        assertEquals(
            true,
            actualProcessIdOutput,
        )
        assertEquals(
            expectedExecutionResult,
            actualExecutionResults[1],
        )

    }

    @Test
    fun executePluginEntryPointEnvironmentNormal() = runBlocking {

        coEvery { environmentRepository.resolveEnvironmentConfig() } returns mapOf(
            "TEST_EXECUTION_VALUE" to "expected",
        )

        val actualExecutionResults = withTimeout(
            timeMillis = 5_000,
        ) {
            pluginExecutionGateway.executePluginEntryPoint(
                pluginEntryPoint = "printenv TEST_EXECUTION_VALUE",
                pluginPackageDirectory = ".",
            ).take(
                count = 2,
            ).toList()
        }

        val expectedExecutionResult = ExecutionResult(
            resultTag = ExecutionResultTag.Normal,
            output = "- expected",
        )

        assertEquals(
            expectedExecutionResult,
            actualExecutionResults[1],
        )

    }

}
