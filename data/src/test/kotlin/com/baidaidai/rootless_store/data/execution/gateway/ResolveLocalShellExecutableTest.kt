package com.baidaidai.rootless_store.data.execution.gateway

import com.topjohnwu.superuser.Shell
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ResolveLocalShellExecutableTest {

    private val pluginExecutionGateway = PluginExecutionGatewayImpl(
        shizukuUserServiceGatewayImpl = mockk(),
        environmentRepositoryImpl = mockk(),
        pluginProcessMonitor = mockk(),
    )
    private val localShell = mockk<Shell>()

    @Before
    fun setup() {

        mockkStatic(Shell::class)
        every { Shell.getShell() } returns localShell

    }

    @After
    fun tearDown() {

        unmockkStatic(Shell::class)

    }

    @Test
    fun resolveLocalShellExecutableRootNormal() {

        every { localShell.isRoot } returns true

        val actualShellExecutable = pluginExecutionGateway.resolveLocalShellExecutable()

        assertEquals(
            "su",
            actualShellExecutable,
        )

    }

    @Test
    fun resolveLocalShellExecutableNonRootNormal() {

        every { localShell.isRoot } returns false

        val actualShellExecutable = pluginExecutionGateway.resolveLocalShellExecutable()

        assertEquals(
            "sh",
            actualShellExecutable,
        )

    }

}
