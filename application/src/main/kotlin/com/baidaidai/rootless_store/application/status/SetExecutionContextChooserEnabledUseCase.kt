package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import javax.inject.Inject

class SetExecutionContextChooserEnabledUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    suspend operator fun invoke(isEnabled: Boolean) =
        storeStatusRepositoryImpl.setExecutionContextChooserEnabled(isEnabled)
}
