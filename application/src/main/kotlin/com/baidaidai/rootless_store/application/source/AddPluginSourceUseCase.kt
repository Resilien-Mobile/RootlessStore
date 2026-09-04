package com.baidaidai.rootless_store.application.source

import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEndpointInput
import javax.inject.Inject

class AddPluginSourceUseCase @Inject constructor(
    private val pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
) {
    suspend operator fun invoke(sourceRemoteEndpoint: String): PluginSourceEvent{
        val sourceEndpointInput = PluginSourceEndpointInput(sourceRemoteEndpoint = sourceRemoteEndpoint)
        return pluginSourceRepositoryImpl.addPluginSource(sourceEndpointInput)
    }
}
