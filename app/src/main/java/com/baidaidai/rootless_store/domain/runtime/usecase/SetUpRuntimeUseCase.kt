package com.baidaidai.rootless_store.domain.runtime.usecase

import com.baidaidai.rootless_store.data.execute.gateway.PluginExecuteGatewayImpl
import com.baidaidai.rootless_store.data.execute.repository.PluginExecuteRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import javax.inject.Inject

class SetUpRuntimeUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginExecuteGatewayImpl: PluginExecuteGatewayImpl,
    private val pluginExecuteRepositoryImpl: PluginExecuteRepositoryImpl,
) {
    suspend operator fun invoke(){
        val pluginExecuteStatusList = pluginExecuteRepositoryImpl.getPluginExecuteStatusList()

        pluginExecuteStatusList.forEach { pluginExecuteStatus ->
            if (pluginExecuteStatus.executeContext == HosterOverallStatus.ADB){
                val abortResult = pluginExecuteGatewayImpl.abortPluginProcessByShizuku(pluginExecuteStatus.executePID)
                if (abortResult){
                    pluginRepositoryImpl.disablePluginByID(pluginExecuteStatus.pluginID)
                    pluginExecuteRepositoryImpl.deleteExecuteRecordByPluginID(pluginExecuteStatus.pluginID)
                }
            }else{
                pluginExecuteGatewayImpl.abortPluginProcess(pluginExecuteStatus.executePID)
                pluginRepositoryImpl.disablePluginByID(pluginExecuteStatus.pluginID)
                pluginExecuteRepositoryImpl.deleteExecuteRecordByPluginID(pluginExecuteStatus.pluginID)
            }
        }
    }
}