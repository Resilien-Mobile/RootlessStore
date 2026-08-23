package com.baidaidai.rootless_store.application.shizuku

import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionAndAuthGatewayImpl
import javax.inject.Inject

class EnsureShizukuPermissionUseCase @Inject constructor(
    private val shizukuPermissionAndAuthGatewayImpl: ShizukuPermissionAndAuthGatewayImpl
) {

    suspend operator fun invoke(): Result<Boolean> {
        return runCatching {
            shizukuPermissionAndAuthGatewayImpl.hasShizukuPermission() || shizukuPermissionAndAuthGatewayImpl.requestShizukuPermission()
        }
    }
}
