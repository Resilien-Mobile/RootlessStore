package com.baidaidai.rootless_store.domain.source.usecase

import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEndpointInput
import javax.inject.Inject

class AddSourceByDefaultUseCase @Inject constructor(
    private val pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
) {
    suspend operator fun invoke(sourceUri: String): PluginSourceEvent{
        val sourceEndpointInput = PluginSourceEndpointInput(sourceRemoteEndpoint = sourceUri)
        return pluginSourceRepositoryImpl.insertPluginSourceByDefault(sourceEndpointInput)
    }
}
