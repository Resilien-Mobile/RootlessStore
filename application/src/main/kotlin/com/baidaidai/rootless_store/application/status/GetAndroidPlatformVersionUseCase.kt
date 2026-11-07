package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.domain.status.repository.StoreStatusRepository
import com.baidaidai.rootless_store.domain.status.model.AndroidPlatformVersion
import javax.inject.Inject

class GetAndroidPlatformVersionUseCase @Inject constructor(
    private val storeStatusRepository: StoreStatusRepository
) {
    operator fun invoke(): AndroidPlatformVersion = storeStatusRepository.getAndroidPlatformVersion()
}
