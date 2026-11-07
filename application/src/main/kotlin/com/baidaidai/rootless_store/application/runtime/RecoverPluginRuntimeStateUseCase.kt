package com.baidaidai.rootless_store.application.runtime

import com.baidaidai.rootless_store.domain.execution.gateway.PluginExecutionGateway
import com.baidaidai.rootless_store.domain.execution.repository.PluginExecutionRepository
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import javax.inject.Inject

class RecoverPluginRuntimeStateUseCase @Inject constructor(
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl,
    private val pluginExecutionGateway: PluginExecutionGateway,
    private val pluginExecutionRepository: PluginExecutionRepository,
) {
    suspend operator fun invoke(){
        val pluginExecutionStatuses = pluginExecutionRepository.listPluginExecutionStatuses()

        pluginExecutionStatuses.forEach { pluginExecutionStatus ->
            if (pluginExecutionStatus.executionContext == ExecutionContext.ADB){
                val isPluginProcessAbortSuccessful = pluginExecutionGateway.abortPluginProcessByShizuku(pluginExecutionStatus.executionPid)
                if (isPluginProcessAbortSuccessful){
                    pluginStatusRepositoryImpl.disablePlugin(pluginExecutionStatus.pluginId)
                    pluginExecutionRepository.deletePluginExecutionByPluginId(pluginExecutionStatus.pluginId)
                }
            }else{
                pluginExecutionGateway.abortPluginProcess(pluginExecutionStatus.executionPid)
                pluginStatusRepositoryImpl.disablePlugin(pluginExecutionStatus.pluginId)
                pluginExecutionRepository.deletePluginExecutionByPluginId(pluginExecutionStatus.pluginId)
            }
        }
    }
}