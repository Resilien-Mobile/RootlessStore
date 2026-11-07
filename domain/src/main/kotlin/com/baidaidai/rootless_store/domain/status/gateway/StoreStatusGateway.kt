package com.baidaidai.rootless_store.domain.status.gateway

import com.baidaidai.rootless_store.domain.status.model.AndroidPlatformVersion
import com.baidaidai.rootless_store.domain.status.model.CpuCoreMetrics
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.NetworkInterfaceMetrics
import com.baidaidai.rootless_store.domain.status.model.SeLinuxStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TemperatureStatus
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface StoreStatusGateway {

    fun observeMemoryStatus(): Flow<MemoryStatus>

    fun observeStorageStatus(): Flow<StorageStatus>

    fun getSeLinuxStatus(): SeLinuxStatus

    fun getKernelVersion(): String

    fun observeTemperatureStatus(): Flow<TemperatureStatus>

    fun getAndroidPlatformVersion(): AndroidPlatformVersion

    fun observeAvailableExecutionContext(): Flow<ExecutionContext>

    fun isRootShellAvailable(): Boolean

    fun isShizukuAvailable(): Boolean

    suspend fun findCpuCoreMetrics(
        cpuCoreIndex: Int? = null
    ): CpuCoreMetrics?

    suspend fun findCpuCoreCount(): Int?

    suspend fun findSystemUptime(): Duration?

    suspend fun findNetworkInterfaceMetrics(
        networkInterfaceName: String = "wlan0"
    ): NetworkInterfaceMetrics?

    fun isCarrierAvailable(): Boolean

    fun isVpnAvailable(): Boolean

    fun findCarrierNetworkInterfaceName(): String?

    fun findVpnNetworkInterfaceName(): String?

}
