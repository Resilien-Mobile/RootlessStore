package com.baidaidai.rootless_store.data.status.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.shizuku.client.ShizukuAuthManager
import com.baidaidai.rootless_store.data.shizuku.client.ShizukuEndpointManager
import com.baidaidai.rootless_store.data.status.datasource.AndroidAndAPIVersionDataSource
import com.baidaidai.rootless_store.data.status.datasource.KernelStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.MemoryStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.SELinuxStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.StorageStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.TemperatureStatusDataSource
import com.baidaidai.rootless_store.domain.status.model.AndroidAndAPIStatus
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.SELinuxStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TempStatus
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StoreStatusGatewayImpl @Inject constructor(
    private val memoryStatusDataSource: MemoryStatusDataSource,
    private val storageStatusDataSource: StorageStatusDataSource,
    private val selinuxStatusDataSource: SELinuxStatusDataSource,
    private val kernelStatusDataSource: KernelStatusDataSource,
    private val temperatureStatusDataSource: TemperatureStatusDataSource,
    private val androidAndAPIVersionDataSource: AndroidAndAPIVersionDataSource,
    private val shizukuEndpointManager: ShizukuEndpointManager
) {
    fun getMemoryStatus(): Flow<MemoryStatus> = flow {
        while (true){
            val totalMemory = memoryStatusDataSource.getTotalMemory()
            val usedMemory = memoryStatusDataSource.getUsedMemory()
            emit(MemoryStatus(totalMemory,usedMemory))
            delay(100)
        }
    }

    fun getStorageStatus(): Flow<StorageStatus> = flow {
        while (true){
            val usedStorage = storageStatusDataSource.getUsedStorage()
            val totalStorage = storageStatusDataSource.getTotalStorage()
            emit(StorageStatus(totalStorage,usedStorage))
            delay(1000)
        }
    }

    fun getSELinuxStatus(): SELinuxStatus = selinuxStatusDataSource.returnSELinuxStatus()

    fun getKernelStatus(): String = kernelStatusDataSource.getDeviceKernel()

    fun getTemperatureStatus(): Flow<TempStatus> = temperatureStatusDataSource.getDeviceTemperatureStatus()

    fun getAndroidAndAPIStatus(): AndroidAndAPIStatus {
        val androidVersion = androidAndAPIVersionDataSource.getAndroidVersion()
        val apiVersion = androidAndAPIVersionDataSource.getAndroidAPIVersion()
        return AndroidAndAPIStatus(androidVersion,apiVersion)
    }

    fun getHosterOverallStatus(): HosterOverallStatus {
        if (Shell.getShell().isRoot) {
            Log.d("HosterOverallStatus", "Root")
            return HosterOverallStatus.ROOTD
        }

        if (ShizukuAuthManager.pingShizuku() && ShizukuAuthManager.checkShizukuPermission()) {
            Log.d("HosterOverallStatus", "Attempt bind Shizuku Endpoint")
            val ok = shizukuEndpointManager.bind()
            Log.d("HosterOverallStatus", "Bind result: $ok")

            if (ok) {
                Log.d("HosterOverallStatus", "Shizuku")
                return HosterOverallStatus.ADB
            }
            // bind 失败：什么都不 return，直接落下去继续判断
        }

        return if (getSELinuxStatus() == SELinuxStatus.Permissive) {
            Log.d("HosterOverallStatus", "Permissive")
            HosterOverallStatus.PERMISSIVE
        } else {
            Log.d("HosterOverallStatus", "Limited")
            HosterOverallStatus.LIMITED
        }
    }

    fun getRootStatus(): Boolean {
        return Shell.getShell().isRoot
    }

    fun getShizukuStatus(): Boolean {
        return shizukuEndpointManager.bind()
    }

}