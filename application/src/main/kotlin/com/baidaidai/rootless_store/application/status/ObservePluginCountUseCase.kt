package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.PluginCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObservePluginCountUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val pluginStatusRepositoryImpl: PluginStatusRepositoryImpl
) {
    operator fun invoke(): Flow<PluginCount> = flow {
        val totalPluginCount = pluginRepositoryImpl.getPluginCount()
        val enabledPluginCount = pluginStatusRepositoryImpl.getEnabledPluginCount()
        emit(PluginCount(totalCount = totalPluginCount, enabledCount = enabledPluginCount))
    }
}
