package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.data.environment.repository.EnvironmentStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.environment.model.EnvironmentStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveEnvironmentStatusUseCase @Inject constructor(
    private val environmentStatusRepositoryImpl: EnvironmentStatusRepositoryImpl
) {
    operator fun invoke(environmentId: String): Flow<EnvironmentStatus?> =
        environmentStatusRepositoryImpl.observeEnvironmentStatus(environmentId)
}
