package com.baidaidai.rootless_store.domain.source.usecase

import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWholeSourceUseCase @Inject constructor(
    private val pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
) {
    operator fun invoke(): Flow<List<PluginSourceInfo>>{
        return pluginSourceRepositoryImpl.getAllPluginSources().map {
            it.orEmpty()
        }
    }
}