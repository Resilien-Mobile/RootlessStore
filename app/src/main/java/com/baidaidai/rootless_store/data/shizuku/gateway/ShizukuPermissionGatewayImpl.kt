package com.baidaidai.rootless_store.data.shizuku.gateway

import android.content.pm.PackageManager
import kotlinx.coroutines.suspendCancellableCoroutine
import rikka.shizuku.Shizuku
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuPermissionGatewayImpl @Inject constructor() {

    private val requestCode = 1001

    /**
     * 通过响应 ping 回答，确保 Shizuku 那边的状态还好
     */
    @Deprecated(
        message = "这个方法不属于 ShizukuPermissionGatewayImpl 的操作范畴，建议尽快转移到更合适的位置。"
    )
    fun pingShizuku(): Boolean{
        return Shizuku.pingBinder()
    }

    /**
     * Check if user already granted the Shizuku permission
     */
    fun hasShizukuPermission(): Boolean{
        return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check Shizuku Permission Granted,
     * Or Try to Access Shizuku Permission
     */
    suspend fun requestShizukuPermission(): Boolean {

        // If Shizuku is no alive, no-need to continue
        if (!Shizuku.pingBinder()) return false
        // If already granted shizuku permission, return true
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) return true

        return suspendCancellableCoroutine { continuation ->
            val listener = object : Shizuku.OnRequestPermissionResultListener {
                override fun onRequestPermissionResult(code: Int, result: Int) {
                    if (code != requestCode) return

                    Shizuku.removeRequestPermissionResultListener(this)

                    if (continuation.isActive) {
                        continuation.resume(result == PackageManager.PERMISSION_GRANTED) {}
                    }
                }
            }

            Shizuku.addRequestPermissionResultListener(listener)
            Shizuku.requestPermission(requestCode)

            continuation.invokeOnCancellation {
                Shizuku.removeRequestPermissionResultListener(listener)
            }
        }
    }
}
