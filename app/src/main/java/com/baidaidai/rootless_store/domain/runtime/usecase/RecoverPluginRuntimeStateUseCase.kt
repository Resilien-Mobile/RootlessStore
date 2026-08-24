package com.baidaidai.rootless_store.domain.runtime.usecase

import com.baidaidai.rootless_store.data.execution.gateway.PluginExecutionGatewayImpl
import com.baidaidai.rootless_store.data.execution.repository.PluginExecutionRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import javax.inject.Inject

class RecoverPluginRuntimeStateUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginExecutionGatewayImpl: PluginExecutionGatewayImpl,
    private val pluginExecutionRepositoryImpl: PluginExecutionRepositoryImpl,
) {
    suspend operator fun invoke(){
        val pluginExecutionStatuses = pluginExecutionRepositoryImpl.listPluginExecutionStatuses()

        pluginExecutionStatuses.forEach { pluginExecutionStatus ->
            if (pluginExecutionStatus.executionContext == ExecutionContext.ADB){
                val isPluginProcessAbortSuccessful = pluginExecutionGatewayImpl.abortPluginProcessByShizuku(pluginExecutionStatus.executionPid)
                if (isPluginProcessAbortSuccessful){
                    pluginRepositoryImpl.disablePlugin(pluginExecutionStatus.pluginId)
                    pluginExecutionRepositoryImpl.deletePluginExecutionByPluginId(pluginExecutionStatus.pluginId)
                }
            }else{
                pluginExecutionGatewayImpl.abortPluginProcess(pluginExecutionStatus.executionPid)
                pluginRepositoryImpl.disablePlugin(pluginExecutionStatus.pluginId)
                pluginExecutionRepositoryImpl.deletePluginExecutionByPluginId(pluginExecutionStatus.pluginId)
            }
        }
    }
}
