package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveExecutionContextPreferenceUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    operator fun invoke(): Flow<ExecutionContext> = storeStatusRepositoryImpl.observeExecutionContextPreference()
}
