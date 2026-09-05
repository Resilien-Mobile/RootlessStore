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
import com.baidaidai.rootless_store.application.shell.GetRootShellAvailabilityUseCase
import com.baidaidai.rootless_store.application.shell.ObserveAdbShellAvailabilityUseCase
import com.baidaidai.rootless_store.application.status.GetAndroidPlatformVersionUseCase
import com.baidaidai.rootless_store.application.status.GetKernelVersionUseCase
import com.baidaidai.rootless_store.application.status.GetSeLinuxStatusUseCase
import com.baidaidai.rootless_store.application.status.ObserveExecutionContextPreferenceUseCase
import com.baidaidai.rootless_store.application.status.ObservePluginCountUseCase
import com.baidaidai.rootless_store.application.status.ObserveTemperatureStatusUseCase
import com.baidaidai.rootless_store.application.status.SetExecutionContextChooserEnabledUseCase
import com.baidaidai.rootless_store.application.status.SetExecutionContextPreferenceUseCase
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
import kotlinx.coroutines.flow.combine
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
    observeExecutionContextPreferenceUseCase: ObserveExecutionContextPreferenceUseCase,
    private val getRootShellAvailabilityUseCase: GetRootShellAvailabilityUseCase,
    private val observeExecutionContextUseCase: ObserveExecutionContextUseCase,
    private val setExecutionContextPreferenceUseCase: SetExecutionContextPreferenceUseCase,
    private val setExecutionContextChooserEnabledUseCase: SetExecutionContextChooserEnabledUseCase,
    private val observeAutoUpdateEnabledPreferenceUseCase: ObserveAutoUpdateEnabledPreferenceUseCase,
    private val fetchLatestVersionTagUseCase: FetchLatestVersionTagUseCase,
    private val observeAdbShellAvailabilityUseCase: ObserveAdbShellAvailabilityUseCase,
    private val observeCpuDashboardConfigUseCase: ObserveCpuDashboardConfigUseCase,
    private val observeNetworkDashboardConfigUseCase: ObserveNetworkDashboardConfigUseCase
) : ViewModel() {

    init {
        refreshLatestVersionTag()
    }

    private val _isContextDialogVisible = MutableStateFlow(false)
    private val _pendingExecutionContext = MutableStateFlow<ExecutionContext?>(null)
    val isContextDialogVisible = _isContextDialogVisible.asStateFlow()

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
    ) { contextPreference, pendingContext ->
        pendingContext ?: contextPreference
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = ExecutionContext.LIMITED
    )

    val isAdbShellAvailable = observeAdbShellAvailabilityUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    private val _isRootShellAvailable = MutableStateFlow(getRootShellAvailabilityUseCase())
    val isRootShellAvailable = _isRootShellAvailable.asStateFlow()

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
