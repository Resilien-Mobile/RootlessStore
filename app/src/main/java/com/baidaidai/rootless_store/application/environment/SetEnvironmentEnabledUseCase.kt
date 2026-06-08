package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import javax.inject.Inject

class SetEnvironmentEnabledUseCase @Inject constructor(
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl
) {
    suspend operator fun invoke(
        environmentID: String,
        environmentEnabledStatus: Boolean
    ){
        if (environmentEnabledStatus){
            environmentRepositoryImpl.enableEnvironmentByID(environmentID)
        }else{
            environmentRepositoryImpl.disableEnvironmentByID(environmentID)
        }
    }
}