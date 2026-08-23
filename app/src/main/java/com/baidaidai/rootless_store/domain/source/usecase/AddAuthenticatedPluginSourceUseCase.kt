package com.baidaidai.rootless_store.domain.source.usecase

import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import javax.inject.Inject

class AddAuthenticatedPluginSourceUseCase @Inject constructor(
    private val pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
){
    suspend operator fun invoke(
        pluginSourceAuthFormInput: PluginSourceAuthFormInput
    ): PluginSourceEvent = pluginSourceRepositoryImpl.addAuthenticatedPluginSource(pluginSourceAuthFormInput)
}
