package com.baidaidai.rootless_store.application.shizuku

import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import javax.inject.Inject

class ActiveShizukuUserServiceUseCase @Inject constructor(
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl
) {
    operator fun invoke(): Result<Unit>{
        return runCatching {
            shizukuUserServiceGatewayImpl.tryBindShizukuUserService()
        }
    }
}