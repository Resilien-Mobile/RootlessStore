package com.baidaidai.rootless_store.data.status.gateway

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.baidaidai.rootless_store.core.datastore.rootlessStorePreferencesDataStore
import com.baidaidai.rootless_store.data.shizuku.client.ShizukuAuthManager
import com.baidaidai.rootless_store.data.shizuku.client.ShizukuUserServiceManager
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class StoreStatusGatewayImpl @Inject constructor(
    private val memoryStatusDataSource: MemoryStatusDataSource,
    private val storageStatusDataSource: StorageStatusDataSource,
    private val selinuxStatusDataSource: SELinuxStatusDataSource,
    private val kernelStatusDataSource: KernelStatusDataSource,
    private val temperatureStatusDataSource: TemperatureStatusDataSource,
    private val androidAndAPIVersionDataSource: AndroidAndAPIVersionDataSource,
    private val shizukuUserServiceManager: ShizukuUserServiceManager,
    @ApplicationContext context: Context
) {

    private val dataStore = context.rootlessStorePreferencesDataStore

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

    fun getHosterOverallStatus():Flow<HosterOverallStatus> = flow {
        while (true){
            val isRoot = Shell.getShell().isRoot
            val isShizukuAvailable =
                if (!isRoot && ShizukuAuthManager.pingShizuku() && ShizukuAuthManager.checkShizukuPermission()) {
                    Log.d("HosterOverallStatus", "Attempt bind Shizuku Endpoint")
                    val ok = shizukuUserServiceManager.bind()
                    Log.d("HosterOverallStatus", "Bind result: $ok")
                    ok
                } else {
                    false
                }

            val status = when {
                isRoot -> {
                    Log.d("HosterOverallStatus", "Root")
                    HosterOverallStatus.ROOTD
                }
                isShizukuAvailable -> {
                    Log.d("HosterOverallStatus", "Shizuku")
                    HosterOverallStatus.ADB
                }
                getSELinuxStatus() == SELinuxStatus.Permissive -> {
                    Log.d("HosterOverallStatus", "Permissive")
                    HosterOverallStatus.PERMISSIVE
                }
                else -> {
                    Log.d("HosterOverallStatus", "Limited")
                    HosterOverallStatus.LIMITED
                }
            }

            emit(status)
            delay(3000)
        }
    }

    fun getRootStatus(): Boolean {
        return Shell.getShell().isRoot
    }

    fun getShizukuStatus(): Boolean {
        return shizukuUserServiceManager.bind()
    }

    fun getExecuteContextPreference(): Flow<HosterOverallStatus> {
        return dataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { preference ->
                preference[EXECUTE_CONTEXT]?.let { HosterOverallStatus.valueOf(it) } ?: HosterOverallStatus.LIMITED
            }
    }

    suspend fun setExecuteContextPreference(hosterOverallStatus: HosterOverallStatus) {
        dataStore.edit { preference ->
            preference[EXECUTE_CONTEXT] = hosterOverallStatus.name
        }
    }

    fun getEnableChooserPreference(): Flow<Boolean> {
        return dataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { preference ->
                preference[ENABLE_CHOOSER] ?: false
            }
    }

    suspend fun setEnableChooserPreference(enableStatus: Boolean) {
        dataStore.edit { preference ->
            preference[ENABLE_CHOOSER] = enableStatus
        }
    }

    private companion object {
        val EXECUTE_CONTEXT = stringPreferencesKey("execute_context")
        val ENABLE_CHOOSER = booleanPreferencesKey("enable_chooser")
    }

}