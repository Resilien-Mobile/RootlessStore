package com.baidaidai.rootless_store.data.status.gateway

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.baidaidai.rootless_store.core.datastore.rootlessStorePreferencesDataStore
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionGatewayImpl
import com.baidaidai.rootless_store.data.status.datasource.AndroidAndApiVersionDataSource
import com.baidaidai.rootless_store.data.status.datasource.CpuStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.KernelVersionDataSource
import com.baidaidai.rootless_store.data.status.datasource.MemoryStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.NetworkStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.SeLinuxStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.StorageStatusDataSource
import com.baidaidai.rootless_store.data.status.datasource.TemperatureStatusDataSource
import com.baidaidai.rootless_store.domain.status.model.AndroidAndApiStatus
import com.baidaidai.rootless_store.domain.status.model.CpuCoreMetrics
import com.baidaidai.rootless_store.domain.status.model.CpuDashboardConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.NetworkInterfaceMetrics
import com.baidaidai.rootless_store.domain.status.model.SeLinuxStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TemperatureStatus
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Duration

class StoreStatusGatewayImpl @Inject constructor(
    private val memoryStatusDataSource: MemoryStatusDataSource,
    private val storageStatusDataSource: StorageStatusDataSource,
    private val seLinuxStatusDataSource: SeLinuxStatusDataSource,
    private val kernelVersionDataSource: KernelVersionDataSource,
    private val temperatureStatusDataSource: TemperatureStatusDataSource,
    private val androidAndApiVersionDataSource: AndroidAndApiVersionDataSource,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
    private val shizukuPermissionGatewayImpl: ShizukuPermissionGatewayImpl,
    private val cpuStatusDataSource: CpuStatusDataSource,
    private val networkStatusDataSource: NetworkStatusDataSource,
    @ApplicationContext context: Context
) {

    private val dataStore = context.rootlessStorePreferencesDataStore

    // Status
    fun observeMemoryStatus(): Flow<MemoryStatus> = flow {
        while (true){
            val totalMemory = memoryStatusDataSource.getTotalMemory()
            val usedMemory = memoryStatusDataSource.getUsedMemory()
            emit(MemoryStatus(totalMemory,usedMemory))
            delay(100)
        }
    }

    fun observeStorageStatus(): Flow<StorageStatus> = flow {
        while (true){
            val usedStorage = storageStatusDataSource.getUsedStorage()
            val totalStorage = storageStatusDataSource.getTotalStorage()
            emit(StorageStatus(totalStorage,usedStorage))
            delay(1000)
        }
    }

    fun getSeLinuxStatus(): SeLinuxStatus = seLinuxStatusDataSource.getSeLinuxStatus()

    fun getKernelVersion(): String = kernelVersionDataSource.getKernelVersion()

    fun observeTemperatureStatus(): Flow<TemperatureStatus> = temperatureStatusDataSource.observeDeviceTemperatureStatus()

    fun getAndroidAndApiStatus(): AndroidAndApiStatus {
        val androidVersion = androidAndApiVersionDataSource.getAndroidVersion()
        val apiVersion = androidAndApiVersionDataSource.getAndroidApiVersion()
        return AndroidAndApiStatus(androidVersion,apiVersion)
    }

    fun observeAvailableExecutionContext(): Flow<ExecutionContext> = flow {
        while (true){
            val isRoot = Shell.getShell().isRoot
            val isShizukuAvailable =
                if (!isRoot && shizukuPermissionGatewayImpl.pingShizuku() && shizukuPermissionGatewayImpl.hasShizukuPermission()) {
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

    fun isRootShellAvailable(): Boolean {
        return Shell.getShell().isRoot
    }

    fun isShizukuAvailable(): Boolean {
        shizukuUserServiceGatewayImpl.startShizukuUserService()
        return true
        // MVP后马上删，会抛error不稳定
    }

    // CPU Status
    suspend fun findCpuCoreMetrics(cpuCoreIndex: Int? = null): CpuCoreMetrics? {
        return cpuStatusDataSource(cpuCoreIndex)
    }

    suspend fun findCpuCoreCount(): Int? {
        return cpuStatusDataSource.findCpuCoreCount()
    }

    suspend fun findSystemUptime(): Duration? {
        return cpuStatusDataSource.findSystemUptime()
    }

    // Network Status
    suspend fun findNetworkInterfaceMetrics(
        networkInterfaceName: String = "wlan0"
    ): NetworkInterfaceMetrics? {
        return networkStatusDataSource(networkInterfaceName)
    }

    fun isCarrierAvailable(): Boolean {
        return networkStatusDataSource.isCarrierAvailable()
    }

    fun isVpnAvailable(): Boolean {
        return networkStatusDataSource.isVpnAvailable()
    }

    fun findCarrierNetworkInterfaceName(): String? {
        return networkStatusDataSource.findCarrierNetworkInterfaceName()
    }

    fun findVpnNetworkInterfaceName(): String? {
        return networkStatusDataSource.findVpnNetworkInterfaceName()
    }

    // Preference
    fun observeExecutionContextPreference(): Flow<ExecutionContext> {
        return dataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { preference ->
                preference[EXECUTION_CONTEXT]?.let { ExecutionContext.valueOf(it) } ?: ExecutionContext.LIMITED
            }
    }

    suspend fun setExecutionContextPreference(executionContext: ExecutionContext) {
        dataStore.edit { preference ->
            preference[EXECUTION_CONTEXT] = executionContext.name
        }
    }

    fun observeExecutionContextChooserEnabled(): Flow<Boolean> {
        return dataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { preference ->
                preference[EXECUTION_CONTEXT_CHOOSER_ENABLED] ?: false
            }
    }

    suspend fun setExecutionContextChooserEnabled(isEnabled: Boolean) {
        dataStore.edit { preference ->
            preference[EXECUTION_CONTEXT_CHOOSER_ENABLED] = isEnabled
        }
    }

    private companion object {
        val EXECUTION_CONTEXT = stringPreferencesKey("execute_context")
        val EXECUTION_CONTEXT_CHOOSER_ENABLED = booleanPreferencesKey("enable_chooser")
    }

}
