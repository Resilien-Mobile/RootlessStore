package com.baidaidai.rootless_store.application.shizuku

import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionGatewayImpl
import javax.inject.Inject

class EnsureShizukuPermissionUseCase @Inject constructor(
    private val shizukuPermissionGatewayImpl: ShizukuPermissionGatewayImpl
) {

    suspend operator fun invoke(): Result<Boolean> {
        return runCatching {
            shizukuPermissionGatewayImpl.hasShizukuPermission() || shizukuPermissionGatewayImpl.requestShizukuPermission()
        }
    }
}
