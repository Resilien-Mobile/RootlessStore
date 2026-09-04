package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveExecutionContextChooserEnabledUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    operator fun invoke(): Flow<Boolean> = storeStatusRepositoryImpl.observeExecutionContextChooserEnabled()
}
