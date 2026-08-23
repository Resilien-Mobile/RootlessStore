package com.baidaidai.rootless_store.domain.status.usecase

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import javax.inject.Inject

class SetExecutionContextPreferenceUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    suspend operator fun invoke(executionContext: ExecutionContext) =
        storeStatusRepositoryImpl.setExecutionContextPreference(executionContext)
}
