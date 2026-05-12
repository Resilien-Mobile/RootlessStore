package com.baidaidai.rootless_store.domain.source.usecase

import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity
import com.baidaidai.rootless_store.data.source.repository.PluginSourceRepositoryImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import javax.inject.Inject

class DeleteOneSourceUseCase @Inject constructor(
    val pluginSourceRepositoryImpl: PluginSourceRepositoryImpl
) {
    suspend operator fun invoke(
        pluginSourceInfo: PluginSourceInfo
    ){
        val pluginSourceEntity = PluginSourceEntity.fromPluginSourceLocal(pluginSourceInfo)
        pluginSourceRepositoryImpl.deleteOnePluginSource(pluginSourceEntity)
    }
}