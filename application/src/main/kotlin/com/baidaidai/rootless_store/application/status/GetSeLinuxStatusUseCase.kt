package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.domain.status.repository.StoreStatusRepository
import com.baidaidai.rootless_store.domain.status.model.SeLinuxStatus
import javax.inject.Inject

class GetSeLinuxStatusUseCase @Inject constructor(
    private val storeStatusRepository: StoreStatusRepository
) {
    operator fun invoke(): SeLinuxStatus = storeStatusRepository.getSeLinuxStatus()
}