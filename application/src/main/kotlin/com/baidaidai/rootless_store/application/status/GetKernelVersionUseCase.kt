package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import javax.inject.Inject

class GetKernelVersionUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
){
    operator fun invoke(): String = storeStatusRepositoryImpl.getKernelVersion()
}
