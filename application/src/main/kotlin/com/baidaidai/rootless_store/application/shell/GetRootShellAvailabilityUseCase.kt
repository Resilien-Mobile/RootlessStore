package com.baidaidai.rootless_store.application.shell

import com.baidaidai.rootless_store.domain.status.repository.StoreStatusRepository
import javax.inject.Inject

class GetRootShellAvailabilityUseCase @Inject constructor(
    private val storeStatusRepository: StoreStatusRepository
) {
    operator fun invoke(): Boolean = storeStatusRepository.isRootShellAvailable()
}
