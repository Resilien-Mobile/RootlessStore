package com.baidaidai.rootless_store.application.shell

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import javax.inject.Inject

class GetRootShellAvailabilityUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    operator fun invoke(): Boolean = storeStatusRepositoryImpl.isRootShellAvailable()
}
