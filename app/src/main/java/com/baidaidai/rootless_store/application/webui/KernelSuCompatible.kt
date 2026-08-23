package com.baidaidai.rootless_store.application.webui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.webkit.JavascriptInterface
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.server.ShizukuEndpointCallback
import org.json.JSONArray
import org.json.JSONObject


class KernelSuCompatible(
    private val context: Context,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl
) {

    @JavascriptInterface
    fun exec(command: String): String {
        val stdout = StringBuilder()
        val stderr = StringBuilder()
        val shizukuUserService = shizukuUserServiceGatewayImpl.findShizukuUserService()

        if (shizukuUserService == null) {
            return result(
                errno = 1,
                stdout = "",
                stderr = "Shizuku user service is not available."
            )
        }

        val callback = ShizukuEndpointCallback(
            onExecuteCallback = { session ->
                stdout.appendLine(session.orEmpty())
            },
            onErrorCallback = { error ->
                stderr.appendLine(error.orEmpty())
            },
            onProcessExitedCallback = {}
        )

        shizukuUserService.command(
            command,
            callback,
            false
        )

        return result(
            errno = if (stderr.isBlank()) 0 else 1,
            stdout = stdout.toString(),
            stderr = stderr.toString()
        )
    }

    @JavascriptInterface
    fun listPackages(type: String?): String {
        val packageInfoList = getInstalledPackages()

        // Filter, ensure every package is either system or user
        val packageNameList = packageInfoList
            .filter { packageInfo ->
                when (type?.lowercase()) {
                    "user" -> !packageInfo.isSystemPackage()
                    "system" -> packageInfo.isSystemPackage()
                    else -> true
                }
            }
            .map { packageInfo ->
                packageInfo.packageName
            }

        return JSONArray(packageNameList).toString()
    }

    private fun getInstalledPackages(): List<PackageInfo> {
        val packageManager = context.packageManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(0)
        }
    }

    private fun PackageInfo.isSystemPackage(): Boolean {
        val systemPackageFlags = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
        val applicationInfoFlags = applicationInfo?.flags ?: 0

        return applicationInfoFlags and systemPackageFlags != 0
    }

    private fun result(
        errno: Int,
        stdout: String,
        stderr: String
    ): String {
        return JSONObject()
            .put("errno", errno)
            .put("stdout", stdout)
            .put("stderr", stderr)
            .toString()
    }

}