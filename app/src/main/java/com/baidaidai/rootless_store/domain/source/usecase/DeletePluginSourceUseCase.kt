package com.baidaidai.rootless_store.domain.source.usecase

import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity
import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import javax.inject.Inject

class DeletePluginSourceUseCase @Inject constructor(
    private val pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
) {
    suspend operator fun invoke(
        pluginSource: PluginSource
    ){
        val pluginSourceEntity = PluginSourceEntity.fromPluginSource(pluginSource)
        pluginSourceRepositoryImpl.deletePluginSource(pluginSourceEntity)
    }
}
