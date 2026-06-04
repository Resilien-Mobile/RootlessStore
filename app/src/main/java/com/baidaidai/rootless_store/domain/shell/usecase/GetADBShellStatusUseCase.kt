package com.baidaidai.rootless_store.domain.shell.usecase

import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetADBShellStatusUseCase @Inject constructor(
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl
) {
    operator fun invoke(): Flow<Boolean> = shizukuUserServiceGatewayImpl.getShizukuUserServiceAvailableStatus()
}