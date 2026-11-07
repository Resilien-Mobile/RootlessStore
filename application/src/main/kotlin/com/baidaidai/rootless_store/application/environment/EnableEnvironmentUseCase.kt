package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentStatusRepository
import javax.inject.Inject

class EnableEnvironmentUseCase @Inject constructor(
    private val environmentStatusRepository: EnvironmentStatusRepository
) {
    suspend operator fun invoke(
        environmentId: String
    ){
        environmentStatusRepository.enableEnvironment(environmentId)
    }
}
