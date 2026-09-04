package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.SeLinuxStatus
import javax.inject.Inject

class GetSeLinuxStatusUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    operator fun invoke(): SeLinuxStatus = storeStatusRepositoryImpl.getSeLinuxStatus()
}