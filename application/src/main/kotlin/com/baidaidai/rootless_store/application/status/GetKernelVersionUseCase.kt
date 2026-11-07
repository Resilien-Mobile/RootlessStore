package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.domain.status.repository.StoreStatusRepository
import javax.inject.Inject

class GetKernelVersionUseCase @Inject constructor(
    private val storeStatusRepository: StoreStatusRepository
){
    operator fun invoke(): String = storeStatusRepository.getKernelVersion()
}
