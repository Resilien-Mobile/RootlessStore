package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import javax.inject.Inject

class EnablePluginUseCase @Inject constructor(
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl
) {
    suspend operator fun invoke(
        pluginId: String
    ){
        pluginStatusRepositoryImpl.enablePlugin(pluginId)
    }
}
