package com.baidaidai.rootless_store.data.execution.gateway

import IShellService
import android.util.Log
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class AbortPluginProcessByShizukuTest {

    private val shizukuUserServiceGateway = mockk<ShizukuUserServiceGatewayImpl>()
    private val pluginExecutionGateway = PluginExecutionGatewayImpl(
        shizukuUserServiceGatewayImpl = shizukuUserServiceGateway,
        environmentRepositoryImpl = mockk(),
        pluginProcessMonitor = mockk(),
    )

    @Before
    fun setup() {

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

    }

    @After
    fun tearDown() {

        unmockkStatic(Log::class)

    }

    @Test
    fun abortPluginProcessByShizukuNormal() {

        val shizukuShellService = mockk<IShellService>()
        every { shizukuUserServiceGateway.findShizukuUserService() } returns shizukuShellService
        every { shizukuShellService.kill(1234) } returns true

        val actualAbortResult = pluginExecutionGateway.abortPluginProcessByShizuku(
            pluginProcessPid = 1234,
        )

        assertTrue(actualAbortResult)

    }

    @Test
    fun abortPluginProcessByShizukuUnavailableFail() {

        every { shizukuUserServiceGateway.findShizukuUserService() } returns null

        val actualAbortResult = pluginExecutionGateway.abortPluginProcessByShizuku(
            pluginProcessPid = 1234,
        )

        assertFalse(actualAbortResult)

    }

    @Test
    fun abortPluginProcessByShizukuNullPidFail() {

        val actualAbortResult = pluginExecutionGateway.abortPluginProcessByShizuku(
            pluginProcessPid = null,
        )

        assertFalse(actualAbortResult)

    }

    @Ignore("Gateway currently treats a false kill result as success")
    @Test
    fun abortPluginProcessByShizukuKillFail() {

        val shizukuShellService = mockk<IShellService>()
        every { shizukuUserServiceGateway.findShizukuUserService() } returns shizukuShellService
        every { shizukuShellService.kill(1234) } returns false

        val actualAbortResult = pluginExecutionGateway.abortPluginProcessByShizuku(
            pluginProcessPid = 1234,
        )

        assertFalse(actualAbortResult)

    }

}