package com.baidaidai.rootless_store.domain.status.usecase

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.AndroidPlatformVersion
import javax.inject.Inject

class GetAndroidPlatformVersionUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
) {
    operator fun invoke(): AndroidPlatformVersion = storeStatusRepositoryImpl.getAndroidPlatformVersion()
}
