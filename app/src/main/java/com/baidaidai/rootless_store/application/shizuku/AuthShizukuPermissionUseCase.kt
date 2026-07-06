package com.baidaidai.rootless_store.application.shizuku

import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionAndAuthGatewayImpl
import javax.inject.Inject

class AuthShizukuPermissionUseCase @Inject constructor(
    private val shizukuPermissionAndAuthGatewayImpl: ShizukuPermissionAndAuthGatewayImpl
) {

    suspend operator fun invoke(): Result<Boolean> {
        return runCatching {
            shizukuPermissionAndAuthGatewayImpl.checkShizukuPermission() || shizukuPermissionAndAuthGatewayImpl.applyShizukuPermission()
        }
    }
}
