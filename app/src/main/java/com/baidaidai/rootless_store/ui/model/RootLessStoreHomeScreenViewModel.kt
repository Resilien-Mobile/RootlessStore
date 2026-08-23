package com.baidaidai.rootless_store.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.status.ObserveCpuDashboardConfigUseCase
import com.baidaidai.rootless_store.application.status.ObserveNetDashboardConfigUseCase
import com.baidaidai.rootless_store.application.status.ObserveOverallStatusUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.ObserveAutoUpdateEnabledPreferenceUseCase
import com.baidaidai.rootless_store.domain.shell.usecase.ObserveAdbShellStatusUseCase
import com.baidaidai.rootless_store.domain.status.model.CpuDashboardConfig
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.NetDashboardConfig
import com.baidaidai.rootless_store.domain.status.model.PluginStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TempStatus
import com.baidaidai.rootless_store.domain.status.usecase.GetAndroidAndApiStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.ObserveExecutionContextPreferenceUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetKernelStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.ObserveMemoryStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.ObservePluginStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetRootStatusUseCase
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
class RootLessStoreHomeScreenViewModel @Inject constructor(
    observeMemoryStatusUseCase: ObserveMemoryStatusUseCase,
    observeStorageStatusUseCase: ObserveStorageStatusUseCase,
    observePluginStatusUseCase: ObservePluginStatusUseCase,
    observeTemperatureStatusUseCase: ObserveTemperatureStatusUseCase,
    getSeLinuxStatusUseCase: GetSeLinuxStatusUseCase,
    getKernelStatusUseCase: GetKernelStatusUseCase,
    getAndroidAndApiStatusUseCase: GetAndroidAndApiStatusUseCase,
    observeExecutionContextPreferenceUseCase: ObserveExecutionContextPreferenceUseCase,
    private val getRootStatusUseCase: GetRootStatusUseCase,
    private val observeOverallStatusUseCase: ObserveOverallStatusUseCase,
    private val setExecutionContextPreferenceUseCase: SetExecutionContextPreferenceUseCase,
    private val setExecutionContextChooserEnabledUseCase: SetExecutionContextChooserEnabledUseCase,
    private val observeAutoUpdateEnabledPreferenceUseCase: ObserveAutoUpdateEnabledPreferenceUseCase,
    private val fetchLatestVersionUseCase: FetchLatestVersionUseCase,
    private val observeAdbShellStatusUseCase: ObserveAdbShellStatusUseCase,
    private val observeCpuDashboardConfigUseCase: ObserveCpuDashboardConfigUseCase,
    private val observeNetDashboardConfigUseCase: ObserveNetDashboardConfigUseCase
) : ViewModel() {

    init {
        fetchLatestVersion()
    }

    private var _dialogStatus = MutableStateFlow(false)
    private val _currentExecutionContextSelected = MutableStateFlow<HosterOverallStatus?>(null)
    val dialogStatus = _dialogStatus.asStateFlow()

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

    val temperatureStatus: StateFlow<TempStatus?> =
        observeTemperatureStatusUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = TempStatus.ERROR
            )

    val executionContextPreference = observeExecutionContextPreferenceUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = HosterOverallStatus.LIMITED
        )

    /**
     * if initial -> get from preference
     * else -> get from user settings
     */
    val currentExecutionContextSelected: StateFlow<HosterOverallStatus> = combine(
        executionContextPreference,
        _currentExecutionContextSelected
    ) { contextPreference, contextSelected ->
        contextSelected ?: contextPreference
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = HosterOverallStatus.LIMITED
    )

    val adbStatus = observeAdbShellStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    private val _rootStatus = MutableStateFlow(getRootStatusUseCase())
    val rootStatus = _rootStatus.asStateFlow()

    private val _seLinuxStatus = MutableStateFlow(getSeLinuxStatusUseCase())
    val seLinuxStatus = _seLinuxStatus.asStateFlow()

    private val _kernelStatus = MutableStateFlow(getKernelStatusUseCase())
    val kernelStatus = _kernelStatus.asStateFlow()

    private val _androidAndApiStatus = MutableStateFlow(getAndroidAndApiStatusUseCase())
    val androidAndApiStatus = _androidAndApiStatus.asStateFlow()

    val overallStatus = observeOverallStatusUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = HosterOverallStatus.LIMITED
    )

    val cpuStatus = observeCpuDashboardConfigUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = CpuDashboardConfig._testOnly_
    )

    val netStatus = observeNetDashboardConfigUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = NetDashboardConfig._testOnly_
    )

    // Saves the user's selected execute context for now.
    // This is only for the UI and is not saved to preferences yet.
    fun setCurrentExecutionContextSelected(hosterOverallStatus: HosterOverallStatus) {
        _currentExecutionContextSelected.value = hosterOverallStatus
    }

    // The real context setting for preferences
    fun setExecutionContextPreference() {
        viewModelScope.launch {
            setExecutionContextChooserEnabledUseCase(true)
            setExecutionContextPreferenceUseCase(_currentExecutionContextSelected.value ?: HosterOverallStatus.LIMITED)
        }

        changeDialogStatus()
    }

    fun revertExecutionContextPreference() {
        viewModelScope.launch {
            setExecutionContextChooserEnabledUseCase(false)
            setCurrentExecutionContextSelected(hosterOverallStatus = overallStatus.first())
        }

        changeDialogStatus()
    }

    fun changeDialogStatus(){
        _dialogStatus.value = !_dialogStatus.value
    }

    fun fetchLatestVersion(){
        viewModelScope.launch {
            val isAutoUpdateEnabled = observeAutoUpdateEnabledPreferenceUseCase().first()
            Log.d("fetchLatestVersion",isAutoUpdateEnabled.toString())

            if(isAutoUpdateEnabled){
                val latestVersion = fetchLatestVersionUseCase()
                _latestVersion.value = latestVersion
            }
        }
    }
}
