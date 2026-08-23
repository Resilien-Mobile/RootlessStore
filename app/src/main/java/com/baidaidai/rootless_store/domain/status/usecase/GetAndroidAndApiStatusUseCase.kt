package com.baidaidai.rootless_store.domain.status.usecase

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.AndroidAndApiStatus
import javax.inject.Inject

class GetAndroidAndApiStatusUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    operator fun invoke(): AndroidAndApiStatus = storeStatusRepositoryImpl.getAndroidAndApiStatus()
}