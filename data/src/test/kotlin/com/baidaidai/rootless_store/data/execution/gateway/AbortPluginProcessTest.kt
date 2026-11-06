package com.baidaidai.rootless_store.data.execution.gateway

import com.topjohnwu.superuser.Shell
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.Process.*

class AbortPluginProcessTest {

    private val pluginExecutionGateway = PluginExecutionGatewayImpl(
        shizukuUserServiceGatewayImpl = mockk(),
        environmentRepositoryImpl = mockk(),
        pluginProcessMonitor = mockk(),
    )

    @Test
    fun abortPluginProcessWithNullPidNormal() {

        pluginExecutionGateway.abortPluginProcess(
            pluginProcessPid = null,
        )

    }

    @Test
    fun abortPluginProcessNormal() {

        val testProcess = ProcessBuilder(
            "sleep",
            "30",
        ).start()

        mockkStatic(Shell::class)
        try {
            val localShell = mockk<Shell>()
            every { Shell.getShell() } returns localShell
            every { localShell.isRoot } returns false

            val testProcessPid = (Process::class.java.getMethod("pid").invoke(testProcess) as Long).toInt()
            pluginExecutionGateway.abortPluginProcess(
                pluginProcessPid = testProcessPid,
            )

            assertTrue(
                "Test process was not terminated",
                testProcess.waitFor(5, TimeUnit.SECONDS),
            )

        } finally {
            testProcess.destroyForcibly()
            unmockkStatic(Shell::class)
        }

    }

}
