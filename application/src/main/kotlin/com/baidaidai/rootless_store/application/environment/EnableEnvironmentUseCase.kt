package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.data.environment.repository.EnvironmentStatusRepositoryImpl
import javax.inject.Inject

class EnableEnvironmentUseCase @Inject constructor(
    private val environmentStatusRepositoryImpl: EnvironmentStatusRepositoryImpl
) {
    suspend operator fun invoke(
        environmentId: String
    ){
        environmentStatusRepositoryImpl.enableEnvironment(environmentId)
    }
}
