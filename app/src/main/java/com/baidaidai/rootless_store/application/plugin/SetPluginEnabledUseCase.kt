package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import javax.inject.Inject

class SetPluginEnabledUseCase @Inject constructor(
    private val pluginInfoRepository: PluginRepositoryImpl
) {
    suspend operator fun invoke(
        pluginId: String,
        isEnabled: Boolean
    ){
        if (isEnabled){
            pluginInfoRepository.enablePluginById(pluginId)
        }else{
            pluginInfoRepository.disablePluginById(pluginId)
        }
    }
}