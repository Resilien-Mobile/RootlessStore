package com.baidaidai.rootless_store.domain.shizuku.gateway

interface ShizukuPermissionGateway {

    fun hasShizukuPermission(): Boolean

    fun canUseShizuku(): Boolean

    suspend fun requestShizukuPermission(): Boolean

}
