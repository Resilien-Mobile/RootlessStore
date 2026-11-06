package com.baidaidai.rootless_store.domain.shizuku.api

interface ShizukuApi {

    fun hasShizukuPermission(): Boolean

    fun isShizukuBinderAvailable(): Boolean

}
