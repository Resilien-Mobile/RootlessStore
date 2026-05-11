package com.baidaidai.rootless_store.domain.status.usecase

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import javax.inject.Inject

class SetExecuteContextPreferenceUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    suspend operator fun invoke(hosterOverallStatus: HosterOverallStatus) =
        storeStatusRepositoryImpl.setExecuteContextPreference(hosterOverallStatus)
}
