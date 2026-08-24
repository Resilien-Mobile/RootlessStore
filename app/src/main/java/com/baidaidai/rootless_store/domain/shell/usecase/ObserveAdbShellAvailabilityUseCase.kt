package com.baidaidai.rootless_store.domain.shell.usecase

import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAdbShellAvailabilityUseCase @Inject constructor(
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl
) {
    operator fun invoke(): Flow<Boolean> = shizukuUserServiceGatewayImpl.observeShizukuUserServiceAvailability()
}
