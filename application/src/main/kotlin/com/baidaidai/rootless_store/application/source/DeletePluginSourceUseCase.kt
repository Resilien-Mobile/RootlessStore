package com.baidaidai.rootless_store.application.source

import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import javax.inject.Inject

class DeletePluginSourceUseCase @Inject constructor(
    private val pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
) {
    suspend operator fun invoke(
        pluginSource: PluginSource
    ){
        pluginSourceRepositoryImpl.deletePluginSource(pluginSource)
    }
}
