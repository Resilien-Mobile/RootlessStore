package com.baidaidai.rootless_store.data.status.repository

import com.baidaidai.rootless_store.data.status.gateway.StoreStatusGatewayImpl
import com.baidaidai.rootless_store.domain.status.model.AndroidAndApiStatus
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.SeLinuxStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TemperatureStatus
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class StoreStatusRepositoryImpl @Inject constructor(
    private val storeStatusGatewayImpl: StoreStatusGatewayImpl
) {

    fun observeStorageStatus(): Flow<StorageStatus> = storeStatusGatewayImpl.observeStorageStatus()

    fun observeMemoryStatus(): Flow<MemoryStatus> = storeStatusGatewayImpl.observeMemoryStatus()

    fun getSeLinuxStatus(): SeLinuxStatus = storeStatusGatewayImpl.getSeLinuxStatus()

    fun getKernelVersion(): String = storeStatusGatewayImpl.getKernelVersion()

    fun observeTemperatureStatus(): Flow<TemperatureStatus> = storeStatusGatewayImpl.observeTemperatureStatus()

    fun getAndroidAndApiStatus(): AndroidAndApiStatus = storeStatusGatewayImpl.getAndroidAndApiStatus()

    fun observeAvailableExecutionContext(): Flow<ExecutionContext> = storeStatusGatewayImpl.observeAvailableExecutionContext()

    fun isRootShellAvailable(): Boolean = storeStatusGatewayImpl.isRootShellAvailable()

    fun isShizukuAvailable(): Boolean = storeStatusGatewayImpl.isShizukuAvailable()

    fun observeExecutionContextPreference(): Flow<ExecutionContext> = storeStatusGatewayImpl.observeExecutionContextPreference()

    suspend fun setExecutionContextPreference(executionContext: ExecutionContext) = storeStatusGatewayImpl.setExecutionContextPreference(executionContext)

    fun observeExecutionContextChooserEnabled(): Flow<Boolean> = storeStatusGatewayImpl.observeExecutionContextChooserEnabled()

    suspend fun setExecutionContextChooserEnabled(isEnabled: Boolean) = storeStatusGatewayImpl.setExecutionContextChooserEnabled(isEnabled)

}
