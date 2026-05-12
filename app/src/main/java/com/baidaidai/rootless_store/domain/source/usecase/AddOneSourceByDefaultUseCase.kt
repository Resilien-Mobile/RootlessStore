package com.baidaidai.rootless_store.domain.source.usecase

import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEndpointInput
import javax.inject.Inject

class AddOneSourceByDefaultUseCase @Inject constructor(
    private val pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
) {
    suspend operator fun invoke(sourceURI: String): PluginSourceEvent{
        val sourceEndpointInput = PluginSourceEndpointInput(sourceRemoteEndpoint = sourceURI)
        return pluginSourceRepositoryImpl.insertOnePluginSourceByDefault(sourceEndpointInput)
    }
}