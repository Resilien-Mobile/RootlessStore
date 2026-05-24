package com.baidaidai.rootless_store.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.data.status.repository.GetOverallStatusUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.GetEnableAutoUpdatePreferenceUseCase
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.PluginStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TempStatus
import com.baidaidai.rootless_store.domain.status.usecase.GetADBStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetAndroidAndAPIStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetExecuteContextPreferenceUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetKernelStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetMemoryStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetPluginStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetRootStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetSELinuxUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetStorageStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetTemperatureStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.SetEnableChooserPreferenceUseCase
import com.baidaidai.rootless_store.domain.status.usecase.SetExecuteContextPreferenceUseCase
import com.baidaidai.rootless_store.domain.update.usecase.GetLatestVersionUseCase
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
    getMemoryStatusUseCase: GetMemoryStatusUseCase,
    getStorageStatusUseCase: GetStorageStatusUseCase,
    getPluginStatusUseCase: GetPluginStatusUseCase,
    getTemperatureStatusUseCase: GetTemperatureStatusUseCase,
    getSELinuxUseCase: GetSELinuxUseCase,
    getKernelStatusUseCase: GetKernelStatusUseCase,
    getAndroidAndAPIStatusUseCase: GetAndroidAndAPIStatusUseCase,
    getExecuteContextPreferenceUseCase: GetExecuteContextPreferenceUseCase,
    private val getADBStatusUseCase: GetADBStatusUseCase,
    private val getRootStatusUseCase: GetRootStatusUseCase,
    private val getOverallStatusUseCase: GetOverallStatusUseCase,
    private val setExecuteContextPreferenceUseCase: SetExecuteContextPreferenceUseCase,
    private val setEnableChooserPreferenceUseCase: SetEnableChooserPreferenceUseCase,
    private val getEnableAutoUpdatePreferenceUseCase: GetEnableAutoUpdatePreferenceUseCase,
    private val getLatestVersionUseCase: GetLatestVersionUseCase
) : ViewModel() {

    init {
        getLatestVersion()
    }

    private var _dialogStatus = MutableStateFlow(false)
    private val _currentExecuteContextSelected = MutableStateFlow<HosterOverallStatus?>(null)
    val dialogStatus = _dialogStatus.asStateFlow()

    // Latest Version Status
    private val _latestVersion = MutableStateFlow<String?>(null)
    val latestVersion = _latestVersion.asStateFlow()


    val memoryStatus: StateFlow<MemoryStatus> =
        getMemoryStatusUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = MemoryStatus()
        )

    val storageStatus: StateFlow<StorageStatus> =
        getStorageStatusUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = StorageStatus()
        )

    val pluginStatus: StateFlow<PluginStatus> =
        getPluginStatusUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = PluginStatus()
        )

    val temperatureStatus: StateFlow<TempStatus?> =
        getTemperatureStatusUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = TempStatus.ERROR
            )

    val executeContextPreference = getExecuteContextPreferenceUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = HosterOverallStatus.LIMITED
        )

    /**
     * if initial -> get from preference
     * else -> get from user settings
     */
    val currentExecuteContextSelected: StateFlow<HosterOverallStatus> = combine(
        executeContextPreference,
        _currentExecuteContextSelected
    ) { contextPreference, contextSelected ->
        contextSelected ?: contextPreference
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = HosterOverallStatus.LIMITED
    )

    private val _adbStatus = MutableStateFlow(getADBStatusUseCase())
    val adbStatus = _adbStatus.asStateFlow()

    private val _rootStatus = MutableStateFlow(getRootStatusUseCase())
    val rootStatus = _rootStatus.asStateFlow()

    private val _seLinuxStatus = MutableStateFlow(getSELinuxUseCase())
    val seLinuxStatus = _seLinuxStatus.asStateFlow()

    private val _kernelStatus = MutableStateFlow(getKernelStatusUseCase())
    val kernelStatus = _kernelStatus.asStateFlow()

    private val _androidAndAPIStatus = MutableStateFlow(getAndroidAndAPIStatusUseCase())
    val androidAndAPIStatus = _androidAndAPIStatus.asStateFlow()

    val overallStatus = getOverallStatusUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = HosterOverallStatus.LIMITED
    )

    // Saves the user's selected execute context for now.
    // This is only for the UI and is not saved to preferences yet.
    fun setCurrentExecuteContextSelected(hosterOverallStatus: HosterOverallStatus) {
        _currentExecuteContextSelected.value = hosterOverallStatus
    }

    // The real context setting for preferences
    fun setExecuteContextPreference() {
        viewModelScope.launch {
            setEnableChooserPreferenceUseCase(true)
            setExecuteContextPreferenceUseCase(_currentExecuteContextSelected.value ?: HosterOverallStatus.LIMITED)
        }

        changeDialogStatus()
    }

    fun revertExecuteContextPreference() {
        viewModelScope.launch {
            setEnableChooserPreferenceUseCase(false)
            setCurrentExecuteContextSelected(hosterOverallStatus = overallStatus.first())
        }

        changeDialogStatus()
    }

    fun changeDialogStatus(){
        _dialogStatus.value = !_dialogStatus.value
    }

    fun getLatestVersion(){
        viewModelScope.launch {
            val enableAutoUpdate = getEnableAutoUpdatePreferenceUseCase().first()
            Log.d("getLatestVersion",enableAutoUpdate.toString())

            if(enableAutoUpdate){
                val latestVersion = getLatestVersionUseCase()
                _latestVersion.value = latestVersion
            }
        }
    }
}