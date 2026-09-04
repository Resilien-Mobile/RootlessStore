package com.baidaidai.rootless_store.application.source

import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import javax.inject.Inject

class AddAuthenticatedPluginSourceUseCase @Inject constructor(
    private val pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
){
    suspend operator fun invoke(
        authenticationInput: PluginSourceAuthenticationInput
    ): PluginSourceEvent = pluginSourceRepositoryImpl.addAuthenticatedPluginSource(authenticationInput)
}
