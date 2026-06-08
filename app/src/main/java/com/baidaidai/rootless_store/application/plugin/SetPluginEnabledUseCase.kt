package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import javax.inject.Inject

class SetPluginEnabledUseCase @Inject constructor(
    private val pluginInfoRepository: PluginRepositoryImpl
) {
    suspend operator fun invoke(
        pluginID: String,
        pluginEnabledStatus: Boolean
    ){
        if (pluginEnabledStatus){
            pluginInfoRepository.enablePluginByID(pluginID)
        }else{
            pluginInfoRepository.disablePluginByID(pluginID)
        }
    }
}