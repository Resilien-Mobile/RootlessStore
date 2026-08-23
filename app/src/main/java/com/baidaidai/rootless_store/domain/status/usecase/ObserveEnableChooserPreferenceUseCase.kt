package com.baidaidai.rootless_store.domain.status.usecase

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveEnableChooserPreferenceUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    operator fun invoke(): Flow<Boolean> = storeStatusRepositoryImpl.observeEnableChooserPreference()
}
