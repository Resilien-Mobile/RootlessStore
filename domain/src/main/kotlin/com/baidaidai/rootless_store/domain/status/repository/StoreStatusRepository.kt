package com.baidaidai.rootless_store.domain.status.repository

import com.baidaidai.rootless_store.domain.status.model.AndroidPlatformVersion
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.SeLinuxStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TemperatureStatus
import kotlinx.coroutines.flow.Flow

interface StoreStatusRepository {

    fun observeStorageStatus(): Flow<StorageStatus>

    fun observeMemoryStatus(): Flow<MemoryStatus>

    fun getSeLinuxStatus(): SeLinuxStatus

    fun getKernelVersion(): String

    fun observeTemperatureStatus(): Flow<TemperatureStatus>

    fun getAndroidPlatformVersion(): AndroidPlatformVersion

    fun observeAvailableExecutionContext(): Flow<ExecutionContext>

    fun isRootShellAvailable(): Boolean

    fun isShizukuAvailable(): Boolean

}
