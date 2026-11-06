package com.baidaidai.rootless_store.data.execution.gateway

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class CommandFactoryTest {

    private val pluginExecutionGateway = PluginExecutionGatewayImpl(
        shizukuUserServiceGatewayImpl = mockk(),
        environmentRepositoryImpl = mockk(),
        pluginProcessMonitor = mockk(),
    )

    @Test
    fun commandFactoryNormal() {

        val actualCommand = pluginExecutionGateway.commandFactory(
            shellExecutable = "sh",
            pluginEntryPoint = "./start.sh",
            pluginPackageDirectory = "/data/plugins/example",
        )

        val expectedCommand = listOf(
            "sh",
            "-c",
            "cd /data/plugins/example ; echo PID:\$\$ ; exec ./start.sh",
        )  // sh -c " cd /data/plugins/example ; echo PID:$$ ; exec ./start.sh "

        assertEquals(
            expectedCommand,
            actualCommand,
        )

    }

}
