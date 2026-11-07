package com.baidaidai.rootless_store.data.status.gateway

import com.baidaidai.rootless_store.domain.status.gateway.StoreStatusGateway
import android.util.Log
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionGatewayImpl
import com.baidaidai.rootless_store.data.status.datasource.AndroidPlatformVersionDataSource
import com.baidaidai.rootless_store.data.status.datasource.CpuStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.KernelVersionDataSource
import com.baidaidai.rootless_store.data.status.datasource.MemoryStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.NetworkStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.SeLinuxStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.StorageStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.TemperatureStatusDataSource
import com.baidaidai.rootless_store.domain.status.model.AndroidPlatformVersion
import com.baidaidai.rootless_store.domain.status.model.CpuCoreMetrics
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.NetworkInterfaceMetrics
import com.baidaidai.rootless_store.domain.status.model.SeLinuxStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TemperatureStatus
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.time.Duration

class StoreStatusGatewayImpl @Inject constructor(
    private val memoryStatusDataSource: MemoryStatusDataSource,
    private val storageStatusDataSource: StorageStatusDataSource,
    private val seLinuxStatusDataSource: SeLinuxStatusDataSource,
    private val kernelVersionDataSource: KernelVersionDataSource,
    private val temperatureStatusDataSource: TemperatureStatusDataSource,
    private val androidPlatformVersionDataSource: AndroidPlatformVersionDataSource,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
    private val shizukuPermissionGatewayImpl: ShizukuPermissionGatewayImpl,
    private val cpuStatusDataSource: CpuStatusDataSource,
    private val networkStatusDataSource: NetworkStatusDataSource
): StoreStatusGateway {

    // Status
    override fun observeMemoryStatus(): Flow<MemoryStatus> = flow {
        while (true){
            val totalMemory = memoryStatusDataSource.getTotalMemory()
            val usedMemory = memoryStatusDataSource.getUsedMemory()
            emit(MemoryStatus(totalMemory,usedMemory))
            delay(100)
        }
    }

    override fun observeStorageStatus(): Flow<StorageStatus> = flow {
        while (true){
            val usedStorage = storageStatusDataSource.getUsedStorage()
            val totalStorage = storageStatusDataSource.getTotalStorage()
            emit(StorageStatus(totalStorage,usedStorage))
            delay(1000)
        }
    }

    override fun getSeLinuxStatus(): SeLinuxStatus = seLinuxStatusDataSource.getSeLinuxStatus()

    override fun getKernelVersion(): String = kernelVersionDataSource.getKernelVersion()

    override fun observeTemperatureStatus(): Flow<TemperatureStatus> = temperatureStatusDataSource.observeDeviceTemperatureStatus()

    override fun getAndroidPlatformVersion(): AndroidPlatformVersion {
        val releaseLabel = androidPlatformVersionDataSource.getReleaseLabel()
        val apiLevelLabel = androidPlatformVersionDataSource.getApiLevelLabel()
        return AndroidPlatformVersion(releaseLabel, apiLevelLabel)
    }

    override fun observeAvailableExecutionContext(): Flow<ExecutionContext> = flow {
        while (true){
            val isRoot = Shell.getShell().isRoot
            val isShizukuAvailable =
                if (!isRoot && ShizukuPermissionGatewayImpl.pingShizuku() && shizukuPermissionGatewayImpl.hasShizukuPermission()) {
                    Log.d("ExecutionContext", "Attempt startShizukuUserService Shizuku Endpoint")
                    val isUserServiceStarted = shizukuUserServiceGatewayImpl.startShizukuUserService()
                    Log.d("ExecutionContext", "Bind result: $isUserServiceStarted")
                    true
                } else {
                    false
                }

            val availableExecutionContext = when {
                isRoot -> {
                    Log.d("ExecutionContext", "Root")
                    ExecutionContext.ROOTD
                }
                isShizukuAvailable -> {
                    Log.d("ExecutionContext", "Shizuku")
                    ExecutionContext.ADB
                }
                getSeLinuxStatus() == SeLinuxStatus.Permissive -> {
                    Log.d("ExecutionContext", "Permissive")
                    ExecutionContext.PERMISSIVE
                }
                else -> {
                    Log.d("ExecutionContext", "Limited")
                    ExecutionContext.LIMITED
                }
            }

            emit(availableExecutionContext)
            delay(3000)
        }
    }

    override fun isRootShellAvailable(): Boolean {
        return Shell.getShell().isRoot
    }

    override fun isShizukuAvailable(): Boolean {
        shizukuUserServiceGatewayImpl.startShizukuUserService()
        return true
        // MVP后马上删，会抛error不稳定
    }

    // CPU Status
    override suspend fun findCpuCoreMetrics(cpuCoreIndex: Int?): CpuCoreMetrics? {
        return cpuStatusDataSource(cpuCoreIndex)
    }

    override suspend fun findCpuCoreCount(): Int? {
        return cpuStatusDataSource.findCpuCoreCount()
    }

    override suspend fun findSystemUptime(): Duration? {
        return cpuStatusDataSource.findSystemUptime()
    }

    // Network Status
    override suspend fun findNetworkInterfaceMetrics(
        networkInterfaceName: String
    ): NetworkInterfaceMetrics? {
        return networkStatusDataSource(networkInterfaceName)
    }

    override fun isCarrierAvailable(): Boolean {
        return networkStatusDataSource.isCarrierAvailable()
    }

    override fun isVpnAvailable(): Boolean {
        return networkStatusDataSource.isVpnAvailable()
    }

    override fun findCarrierNetworkInterfaceName(): String? {
        return networkStatusDataSource.findCarrierNetworkInterfaceName()
    }

    override fun findVpnNetworkInterfaceName(): String? {
        return networkStatusDataSource.findVpnNetworkInterfaceName()
    }

}
