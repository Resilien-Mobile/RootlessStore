package com.baidaidai.rootless_store.data.execution.mapper

import com.baidaidai.rootless_store.data.execution.database.PluginExecutionEntity
import com.baidaidai.rootless_store.data.execution.mapper.PluginExecutionMapper.toPluginExecutionStatus
import com.baidaidai.rootless_store.domain.execution.model.PluginExecutionStatus
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import org.junit.Assert.assertEquals
import org.junit.Test

class PluginExecutionMapperTest {

    @Test
    fun pluginExecutionEntityToPluginExecutionStatusTest() {
        val fakePluginExecutionEntity = PluginExecutionEntity(
            pluginId = "plugin.power.menu",
            executionState = PluginState.Great,
            executionPid = 2048,
            executionContext = ExecutionContext.ROOTD
        )

        val pluginExecutionStatus = fakePluginExecutionEntity.toPluginExecutionStatus()

        val expectedPluginExecutionStatus = PluginExecutionStatus(
            pluginId = "plugin.power.menu",
            executionState = PluginState.Great,
            executionPid = 2048,
            executionContext = ExecutionContext.ROOTD
        )
        assertEquals(expectedPluginExecutionStatus, pluginExecutionStatus)
    }
}
