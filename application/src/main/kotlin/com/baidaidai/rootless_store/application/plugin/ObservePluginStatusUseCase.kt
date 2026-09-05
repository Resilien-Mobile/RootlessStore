package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.model.PluginStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePluginStatusUseCase @Inject constructor(
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl
) {
    operator fun invoke(pluginId: String): Flow<PluginStatus?> =
        pluginStatusRepositoryImpl.observePluginStatus(pluginId)
}
