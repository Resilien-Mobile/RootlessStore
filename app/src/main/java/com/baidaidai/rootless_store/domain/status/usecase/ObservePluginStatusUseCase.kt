package com.baidaidai.rootless_store.domain.status.usecase

import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.PluginStatus
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ObservePluginStatusUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl
){
    operator fun invoke(): Flow<PluginStatus> = flow {
        val totalPluginCount = pluginRepositoryImpl.getPluginCount()
        val enabledPluginCount = pluginRepositoryImpl.getEnabledPluginCount()
        emit(PluginStatus(totalCount = totalPluginCount, enabledCount = enabledPluginCount))
    }
}
