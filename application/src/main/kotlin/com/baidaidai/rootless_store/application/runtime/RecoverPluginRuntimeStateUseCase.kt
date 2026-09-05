package com.baidaidai.rootless_store.application.runtime

import com.baidaidai.rootless_store.data.execution.gateway.PluginExecutionGatewayImpl
import com.baidaidai.rootless_store.data.execution.repository.PluginExecutionRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import javax.inject.Inject

class RecoverPluginRuntimeStateUseCase @Inject constructor(
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl,
    private val pluginExecutionGatewayImpl: PluginExecutionGatewayImpl,
    private val pluginExecutionRepositoryImpl: PluginExecutionRepositoryImpl,
) {
    suspend operator fun invoke(){
        val pluginExecutionStatuses = pluginExecutionRepositoryImpl.listPluginExecutionStatuses()

        pluginExecutionStatuses.forEach { pluginExecutionStatus ->
            if (pluginExecutionStatus.executionContext == ExecutionContext.ADB){
                val isPluginProcessAbortSuccessful = pluginExecutionGatewayImpl.abortPluginProcessByShizuku(pluginExecutionStatus.executionPid)
                if (isPluginProcessAbortSuccessful){
                    pluginStatusRepositoryImpl.disablePlugin(pluginExecutionStatus.pluginId)
                    pluginExecutionRepositoryImpl.deletePluginExecutionByPluginId(pluginExecutionStatus.pluginId)
                }
            }else{
                pluginExecutionGatewayImpl.abortPluginProcess(pluginExecutionStatus.executionPid)
                pluginStatusRepositoryImpl.disablePlugin(pluginExecutionStatus.pluginId)
                pluginExecutionRepositoryImpl.deletePluginExecutionByPluginId(pluginExecutionStatus.pluginId)
            }
        }
    }
}