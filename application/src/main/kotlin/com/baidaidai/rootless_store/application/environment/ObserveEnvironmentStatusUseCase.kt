package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentStatusRepository
import com.baidaidai.rootless_store.domain.environment.model.EnvironmentStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveEnvironmentStatusUseCase @Inject constructor(
    private val environmentStatusRepository: EnvironmentStatusRepository
) {
    operator fun invoke(environmentId: String): Flow<EnvironmentStatus?> =
        environmentStatusRepository.observeEnvironmentStatus(environmentId)
}
