package com.baidaidai.rootless_store.application.shizuku

import com.baidaidai.rootless_store.domain.shizuku.gateway.ShizukuPermissionGateway
import javax.inject.Inject

class EnsureShizukuPermissionUseCase @Inject constructor(
    private val shizukuPermissionGateway: ShizukuPermissionGateway
) {

    suspend operator fun invoke(): Result<Boolean> {
        return runCatching {
            shizukuPermissionGateway.hasShizukuPermission() || shizukuPermissionGateway.requestShizukuPermission()
        }
    }
}
