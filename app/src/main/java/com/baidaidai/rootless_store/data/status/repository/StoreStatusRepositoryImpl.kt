package com.baidaidai.rootless_store.data.status.repository

import com.baidaidai.rootless_store.data.status.gateway.StoreStatusGatewayImpl
import com.baidaidai.rootless_store.domain.status.model.AndroidAndAPIStatus
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.SELinuxStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TempStatus
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class StoreStatusRepositoryImpl @Inject constructor(
    private val storeStatusGatewayImpl: StoreStatusGatewayImpl
) {

    fun getStorageStatus(): Flow<StorageStatus> = storeStatusGatewayImpl.getStorageStatus()

    fun getMemoryStatus(): Flow<MemoryStatus> = storeStatusGatewayImpl.getMemoryStatus()

    fun getSELinuxStatus(): SELinuxStatus = storeStatusGatewayImpl.getSELinuxStatus()

    fun getKernelStatus(): String = storeStatusGatewayImpl.getKernelStatus()

    fun getTemperatureStatus(): Flow<TempStatus> = storeStatusGatewayImpl.getTemperatureStatus()

    fun getAndroidAndAPIStatus(): AndroidAndAPIStatus = storeStatusGatewayImpl.getAndroidAndAPIStatus()

    fun getOverallStatus(): Flow<HosterOverallStatus> = storeStatusGatewayImpl.getHosterOverallStatus()

    fun getRootStatus(): Boolean = storeStatusGatewayImpl.getRootStatus()

    fun getShizukuStatus(): Boolean = storeStatusGatewayImpl.getShizukuStatus()

    fun getExecuteContextPreference(): Flow<HosterOverallStatus> = storeStatusGatewayImpl.getExecuteContextPreference()

    suspend fun setExecuteContextPreference(hosterOverallStatus: HosterOverallStatus) = storeStatusGatewayImpl.setExecuteContextPreference(hosterOverallStatus)

    fun getEnableChooserPreference(): Flow<Boolean> = storeStatusGatewayImpl.getEnableChooserPreference()

    suspend fun setEnableChooserPreference(chooserStatus: Boolean) = storeStatusGatewayImpl.setEnableChooserPreference(chooserStatus)

}
