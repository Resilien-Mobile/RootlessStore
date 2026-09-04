package com.baidaidai.rootless_store.application.source

import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePluginSourceCountUseCase @Inject constructor(
    private val pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
) {
    operator fun invoke(): Flow<Int>{
        return pluginSourceRepositoryImpl.observePluginSourceCount()
    }
}
