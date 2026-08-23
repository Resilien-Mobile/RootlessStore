package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import javax.inject.Inject

class SetEnvironmentEnabledUseCase @Inject constructor(
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl
) {
    suspend operator fun invoke(
        environmentId: String,
        isEnabled: Boolean
    ){
        if (isEnabled){
            environmentRepositoryImpl.enableEnvironmentById(environmentId)
        }else{
            environmentRepositoryImpl.disableEnvironmentById(environmentId)
        }
    }
}