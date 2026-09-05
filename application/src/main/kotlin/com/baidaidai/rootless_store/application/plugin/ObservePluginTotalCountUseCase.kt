package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePluginTotalCountUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl
) {
    operator fun invoke(): Flow<Int> = pluginRepositoryImpl.observePluginCount()
}
