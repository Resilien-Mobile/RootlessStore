package com.baidaidai.rootless_store.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.status.ObserveCpuDashboardConfigUseCase
import com.baidaidai.rootless_store.application.status.ObserveNetworkDashboardConfigUseCase
import com.baidaidai.rootless_store.application.status.ObserveExecutionContextUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.ObserveAutoUpdateEnabledPreferenceUseCase
import com.baidaidai.rootless_store.domain.shell.usecase.GetRootShellAvailabilityUseCase
import com.baidaidai.rootless_store.domain.shell.usecase.ObserveAdbShellStatusUseCase
import com.baidaidai.rootless_store.domain.status.model.CpuDashboardConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.NetworkDashboardConfig
import com.baidaidai.rootless_store.domain.status.model.PluginStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TemperatureStatus
import com.baidaidai.rootless_store.domain.status.usecase.GetAndroidAndApiStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.ObserveExecutionContextPreferenceUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetKernelStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.ObserveMemoryStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.ObservePluginStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetSeLinuxStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.ObserveStorageStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.ObserveTemperatureStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.SetExecutionContextChooserEnabledUseCase
import com.baidaidai.rootless_store.domain.status.usecase.SetExecutionContextPreferenceUseCase
import com.baidaidai.rootless_store.domain.update.usecase.FetchLatestVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RootlessStoreHomeScreenViewModel @Inject constructor(
    observeMemoryStatusUseCase: ObserveMemoryStatusUseCase,
    observeStorageStatusUseCase: ObserveStorageStatusUseCase,
    observePluginStatusUseCase: ObservePluginStatusUseCase,
    observeTemperatureStatusUseCase: ObserveTemperatureStatusUseCase,
    getSeLinuxStatusUseCase: GetSeLinuxStatusUseCase,
    getKernelStatusUseCase: GetKernelStatusUseCase,
    getAndroidAndApiStatusUseCase: GetAndroidAndApiStatusUseCase,
    observeExecutionContextPreferenceUseCase: ObserveExecutionContextPreferenceUseCase,
    private val getRootShellAvailabilityUseCase: GetRootShellAvailabilityUseCase,
    private val observeExecutionContextUseCase: ObserveExecutionContextUseCase,
    private val setExecutionContextPreferenceUseCase: SetExecutionContextPreferenceUseCase,
    private val setExecutionContextChooserEnabledUseCase: SetExecutionContextChooserEnabledUseCase,
    private val observeAutoUpdateEnabledPreferenceUseCase: ObserveAutoUpdateEnabledPreferenceUseCase,
    private val fetchLatestVersionUseCase: FetchLatestVersionUseCase,
    private val observeAdbShellStatusUseCase: ObserveAdbShellStatusUseCase,
    private val observeCpuDashboardConfigUseCase: ObserveCpuDashboardConfigUseCase,
    private val observeNetworkDashboardConfigUseCase: ObserveNetworkDashboardConfigUseCase
) : ViewModel() {

    init {
        refreshLatestVersion()
    }

    private val _isContextDialogVisible = MutableStateFlow(false)
    private val _pendingExecutionContext = MutableStateFlow<ExecutionContext?>(null)
    val isContextDialogVisible = _isContextDialogVisible.asStateFlow()

    // Latest Version Status
    private val _latestVersion = MutableStateFlow<String?>(null)
    val latestVersion = _latestVersion.asStateFlow()


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

    val pluginStatus: StateFlow<PluginStatus> =
        observePluginStatusUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = PluginStatus()
        )

    val temperatureStatus: StateFlow<TemperatureStatus?> =
        observeTemperatureStatusUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = TemperatureStatus.ERROR
            )

    val executionContextPreference = observeExecutionContextPreferenceUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = ExecutionContext.LIMITED
        )

    /**
     * if initial -> get from preference
     * else -> get from user settings
     */
    val selectedExecutionContext: StateFlow<ExecutionContext> = combine(
        executionContextPreference,
        _pendingExecutionContext
    ) { contextPreference, contextSelected ->
        contextSelected ?: contextPreference
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = ExecutionContext.LIMITED
    )

    val isAdbShellAvailable = observeAdbShellStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    private val _isRootShellAvailable = MutableStateFlow(getRootShellAvailabilityUseCase())
    val isRootShellAvailable = _isRootShellAvailable.asStateFlow()

    private val _seLinuxStatus = MutableStateFlow(getSeLinuxStatusUseCase())
    val seLinuxStatus = _seLinuxStatus.asStateFlow()

    private val _kernelStatus = MutableStateFlow(getKernelStatusUseCase())
    val kernelStatus = _kernelStatus.asStateFlow()

    private val _androidAndApiStatus = MutableStateFlow(getAndroidAndApiStatusUseCase())
    val androidAndApiStatus = _androidAndApiStatus.asStateFlow()

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

    // Saves the user's selected execute context for now.
    // This is only for the UI and is not saved to preferences yet.
    fun selectExecutionContext(executionContext: ExecutionContext) {
        _pendingExecutionContext.value = executionContext
    }

    // The real context setting for preferences
    fun setExecutionContextPreference() {
        viewModelScope.launch {
            setExecutionContextChooserEnabledUseCase(true)
            setExecutionContextPreferenceUseCase(_pendingExecutionContext.value ?: ExecutionContext.LIMITED)
        }

        toggleContextDialogVisibility()
    }

    fun resetExecutionContextPreference() {
        viewModelScope.launch {
            setExecutionContextChooserEnabledUseCase(false)
            selectExecutionContext(executionContext = executionContext.first())
        }

        toggleContextDialogVisibility()
    }

    fun toggleContextDialogVisibility(){
        _isContextDialogVisible.value = !_isContextDialogVisible.value
    }

    fun refreshLatestVersion(){
        viewModelScope.launch {
            val isAutoUpdateEnabled = observeAutoUpdateEnabledPreferenceUseCase().first()
            Log.d("refreshLatestVersion",isAutoUpdateEnabled.toString())

            if(isAutoUpdateEnabled){
                val latestVersion = fetchLatestVersionUseCase()
                _latestVersion.value = latestVersion
            }
        }
    }
}
