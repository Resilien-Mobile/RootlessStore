package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.data.status.repository.GetOverallStatusUseCase
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import com.baidaidai.rootless_store.domain.status.model.PluginStatus
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import com.baidaidai.rootless_store.domain.status.model.TempStatus
import com.baidaidai.rootless_store.domain.status.usecase.GetAndroidAndAPIStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetKernelStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetMemoryStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetPluginStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetSELinuxUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetStorageStatusUseCase
import com.baidaidai.rootless_store.domain.status.usecase.GetTemperatureStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val getOverallStatusUseCase: GetOverallStatusUseCase
) : ViewModel() {

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

    private val _seLinuxStatus = MutableStateFlow(getSELinuxUseCase())
    val seLinuxStatus = _seLinuxStatus.asStateFlow()

    private val _kernelStatus = MutableStateFlow(getKernelStatusUseCase())
    val kernelStatus = _kernelStatus.asStateFlow()

    private val _androidAndAPIStatus = MutableStateFlow(getAndroidAndAPIStatusUseCase())
    val androidAndAPIStatus = _androidAndAPIStatus.asStateFlow()

    private val _overallStatus = MutableStateFlow(getOverallStatusUseCase())
    val overallStatus = _overallStatus.asStateFlow()

    fun refreshHosterOverallStatus() {
        _overallStatus.value = getOverallStatusUseCase()
    }


}