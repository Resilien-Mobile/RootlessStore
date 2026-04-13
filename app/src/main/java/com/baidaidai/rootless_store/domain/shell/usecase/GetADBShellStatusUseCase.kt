package com.baidaidai.rootless_store.domain.shell.usecase

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import javax.inject.Inject

class GetADBShellStatusUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    operator fun invoke () = storeStatusRepositoryImpl.getShizukuStatus()
}