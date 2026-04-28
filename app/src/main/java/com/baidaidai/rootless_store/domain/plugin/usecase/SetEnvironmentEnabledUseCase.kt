package com.baidaidai.rootless_store.domain.plugin.usecase

import com.baidaidai.rootless_store.data.plugin.repository.PluginCoreRepositoryImpl
import javax.inject.Inject

class SetEnvironmentEnabledUseCase @Inject constructor(
    private val pluginInfoRepository: PluginCoreRepositoryImpl
) {
    suspend operator fun invoke(
        environmentID: String,
        environmentEnabledStatus: Boolean
    ){
        if (environmentEnabledStatus){
            pluginInfoRepository.enableEnvironmentByID(environmentID)
        }else{
            pluginInfoRepository.disableEnvironmentByID(environmentID)
        }
    }
}