package com.baidaidai.rootless_store.domain.status.usecase

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import javax.inject.Inject

class ObserveTemperatureStatusUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    operator fun invoke() = storeStatusRepositoryImpl.observeTemperatureStatus()
}
