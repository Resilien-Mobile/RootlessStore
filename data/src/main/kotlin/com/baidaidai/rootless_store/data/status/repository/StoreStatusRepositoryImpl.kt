package com.baidaidai.rootless_store.data.status.repository

import com.baidaidai.rootless_store.domain.status.repository.StoreStatusRepository
import com.baidaidai.rootless_store.data.status.gateway.StoreStatusGatewayImpl
import com.baidaidai.rootless_store.domain.status.model.AndroidPlatformVersion
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.SeLinuxStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TemperatureStatus
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class StoreStatusRepositoryImpl @Inject constructor(
    private val storeStatusGatewayImpl: StoreStatusGatewayImpl
): StoreStatusRepository {

    override fun observeStorageStatus(): Flow<StorageStatus> = storeStatusGatewayImpl.observeStorageStatus()

    override fun observeMemoryStatus(): Flow<MemoryStatus> = storeStatusGatewayImpl.observeMemoryStatus()

    override fun getSeLinuxStatus(): SeLinuxStatus = storeStatusGatewayImpl.getSeLinuxStatus()

    override fun getKernelVersion(): String = storeStatusGatewayImpl.getKernelVersion()

    override fun observeTemperatureStatus(): Flow<TemperatureStatus> = storeStatusGatewayImpl.observeTemperatureStatus()

    override fun getAndroidPlatformVersion(): AndroidPlatformVersion = storeStatusGatewayImpl.getAndroidPlatformVersion()

    override fun observeAvailableExecutionContext(): Flow<ExecutionContext> = storeStatusGatewayImpl.observeAvailableExecutionContext()

    override fun isRootShellAvailable(): Boolean = storeStatusGatewayImpl.isRootShellAvailable()

    override fun isShizukuAvailable(): Boolean = storeStatusGatewayImpl.isShizukuAvailable()

}
