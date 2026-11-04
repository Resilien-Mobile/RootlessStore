package com.baidaidai.rootless_store.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.status.ObserveCpuDashboardConfigUseCase
import com.baidaidai.rootless_store.application.status.ObserveNetworkDashboardConfigUseCase
import com.baidaidai.rootless_store.application.status.ObserveExecutionContextUseCase
import com.baidaidai.rootless_store.application.status.ObserveMemoryStatusUseCase
import com.baidaidai.rootless_store.application.status.ObserveStorageStatusUseCase
import com.baidaidai.rootless_store.application.setting.ObserveAutoUpdateEnabledPreferenceUseCase
import com.baidaidai.rootless_store.application.status.GetAndroidPlatformVersionUseCase
import com.baidaidai.rootless_store.application.status.GetKernelVersionUseCase
import com.baidaidai.rootless_store.application.status.GetSeLinuxStatusUseCase
import com.baidaidai.rootless_store.application.status.ObservePluginCountUseCase
import com.baidaidai.rootless_store.application.status.ObserveTemperatureStatusUseCase
import com.baidaidai.rootless_store.application.version.FetchLatestVersionTagUseCase
import com.baidaidai.rootless_store.domain.status.model.CpuDashboardConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.NetworkDashboardConfig
import com.baidaidai.rootless_store.domain.status.model.PluginCount
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TemperatureStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RootlessStoreHomeScreenViewModel @Inject constructor(
    observeMemoryStatusUseCase: ObserveMemoryStatusUseCase,
    observeStorageStatusUseCase: ObserveStorageStatusUseCase,
    observePluginCountUseCase: ObservePluginCountUseCase,
    observeTemperatureStatusUseCase: ObserveTemperatureStatusUseCase,
    getSeLinuxStatusUseCase: GetSeLinuxStatusUseCase,
    getKernelVersionUseCase: GetKernelVersionUseCase,
    getAndroidPlatformVersionUseCase: GetAndroidPlatformVersionUseCase,
    private val observeExecutionContextUseCase: ObserveExecutionContextUseCase,
    private val observeAutoUpdateEnabledPreferenceUseCase: ObserveAutoUpdateEnabledPreferenceUseCase,
    private val fetchLatestVersionTagUseCase: FetchLatestVersionTagUseCase,
    private val observeCpuDashboardConfigUseCase: ObserveCpuDashboardConfigUseCase,
    private val observeNetworkDashboardConfigUseCase: ObserveNetworkDashboardConfigUseCase
) : ViewModel() {

    init {
        refreshLatestVersionTag()
    }

    // Latest release tag
    private val _latestVersionTag = MutableStateFlow<String?>(null)
    val latestVersionTag = _latestVersionTag.asStateFlow()


    val memoryStatus: StateFlow<MemoryStatus> =
        observeMemoryStatusUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = MemoryStatus()
        )

    val storageStatus: StateFlow<StorageStatus> =
        observeStorageStatusUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = StorageStatus()
        )

    val pluginCount: StateFlow<PluginCount> =
        observePluginCountUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = PluginCount()
        )

    val temperatureStatus: StateFlow<TemperatureStatus?> =
        observeTemperatureStatusUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = TemperatureStatus.ERROR
            )

    private val _seLinuxStatus = MutableStateFlow(getSeLinuxStatusUseCase())
    val seLinuxStatus = _seLinuxStatus.asStateFlow()

    private val _kernelVersion = MutableStateFlow(getKernelVersionUseCase())
    val kernelVersion = _kernelVersion.asStateFlow()

    private val _androidPlatformVersion = MutableStateFlow(getAndroidPlatformVersionUseCase())
    val androidPlatformVersion = _androidPlatformVersion.asStateFlow()

    val executionContext = observeExecutionContextUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = ExecutionContext.LIMITED
    )

    val cpuDashboardConfig = observeCpuDashboardConfigUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = CpuDashboardConfig._testOnly_
    )

    val networkDashboardConfig = observeNetworkDashboardConfigUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = NetworkDashboardConfig._testOnly_
    )

    fun refreshLatestVersionTag(){
        viewModelScope.launch {
            val isAutoUpdateEnabled = observeAutoUpdateEnabledPreferenceUseCase().first()
            Log.d("refreshLatestVersionTag",isAutoUpdateEnabled.toString())

            if(isAutoUpdateEnabled){
                val latestVersionTag = fetchLatestVersionTagUseCase()
                _latestVersionTag.value = latestVersionTag
            }
        }
    }
}
