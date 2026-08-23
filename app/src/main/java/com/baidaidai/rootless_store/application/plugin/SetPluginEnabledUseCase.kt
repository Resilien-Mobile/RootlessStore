package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import javax.inject.Inject

class SetPluginEnabledUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl
) {
    suspend operator fun invoke(
        pluginId: String,
        isEnabled: Boolean
    ){
        if (isEnabled){
            pluginRepositoryImpl.enablePluginById(pluginId)
        }else{
            pluginRepositoryImpl.disablePluginById(pluginId)
        }
    }
}