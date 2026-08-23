package com.baidaidai.rootless_store.data.status.repository

import com.baidaidai.rootless_store.data.status.gateway.StoreStatusGatewayImpl
import com.baidaidai.rootless_store.domain.status.model.AndroidAndApiStatus
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.SeLinuxStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TempStatus
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class StoreStatusRepositoryImpl @Inject constructor(
    private val storeStatusGatewayImpl: StoreStatusGatewayImpl
) {

    fun observeStorageStatus(): Flow<StorageStatus> = storeStatusGatewayImpl.observeStorageStatus()

    fun observeMemoryStatus(): Flow<MemoryStatus> = storeStatusGatewayImpl.observeMemoryStatus()

    fun getSeLinuxStatus(): SeLinuxStatus = storeStatusGatewayImpl.getSeLinuxStatus()

    fun getKernelStatus(): String = storeStatusGatewayImpl.getKernelStatus()

    fun observeTemperatureStatus(): Flow<TempStatus> = storeStatusGatewayImpl.observeTemperatureStatus()

    fun getAndroidAndApiStatus(): AndroidAndApiStatus = storeStatusGatewayImpl.getAndroidAndApiStatus()

    fun observeOverallStatus(): Flow<HosterOverallStatus> = storeStatusGatewayImpl.observeHosterOverallStatus()

    fun getRootStatus(): Boolean = storeStatusGatewayImpl.getRootStatus()

    fun getShizukuStatus(): Boolean = storeStatusGatewayImpl.getShizukuStatus()

    fun observeExecutionContextPreference(): Flow<HosterOverallStatus> = storeStatusGatewayImpl.observeExecutionContextPreference()

    suspend fun setExecutionContextPreference(hosterOverallStatus: HosterOverallStatus) = storeStatusGatewayImpl.setExecutionContextPreference(hosterOverallStatus)

    fun observeEnableChooserPreference(): Flow<Boolean> = storeStatusGatewayImpl.observeEnableChooserPreference()

    suspend fun setEnableChooserPreference(chooserStatus: Boolean) = storeStatusGatewayImpl.setEnableChooserPreference(chooserStatus)

}
