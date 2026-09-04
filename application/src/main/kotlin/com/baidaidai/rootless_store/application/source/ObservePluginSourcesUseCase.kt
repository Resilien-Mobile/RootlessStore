package com.baidaidai.rootless_store.application.source

import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObservePluginSourcesUseCase @Inject constructor(
    private val pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
) {
    operator fun invoke(): Flow<List<PluginSource>>{
        return pluginSourceRepositoryImpl.observePluginSources().map {
            it.orEmpty()
        }
    }
}
