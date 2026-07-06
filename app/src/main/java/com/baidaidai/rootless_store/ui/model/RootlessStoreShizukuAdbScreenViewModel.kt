package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.shizuku.ActiveShizukuUserServiceUseCase
import com.baidaidai.rootless_store.application.shizuku.AuthShizukuPermissionUseCase
import com.baidaidai.rootless_store.core.util.OutOfStringLike
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootlessStoreShizukuAdbScreenViewModel @Inject constructor(
    private val activeShizukuUserServiceUseCase: ActiveShizukuUserServiceUseCase,
    private val authShizukuPermissionUseCase: AuthShizukuPermissionUseCase
): ViewModel() {

    private val _shizukuActived = MutableStateFlow(false)
    private val _endpointActived = MutableStateFlow(false)
    val shizukuActived = _shizukuActived.asStateFlow()
    val endpointActived = _endpointActived.asStateFlow()

    private val _shizukuEvent = MutableSharedFlow<PluginError?>()
    val shizukuEvent = _shizukuEvent.asSharedFlow()


    fun onOkButtonClick() = viewModelScope.launch {
        _shizukuEvent.emit(null)
    }

    fun authShizukuPermission() {
        viewModelScope.launch {
            authShizukuPermissionUseCase()
                .onSuccess { shizukuActived -> _shizukuActived.value = shizukuActived }
                .onFailure { error -> _shizukuEvent.emit(PluginError(errorMessage = error.message!!, errorCause = error.stackTrace.OutOfStringLike())) }
        }
    }

    /**
     * Active and connect to Shizuku UserService
     *
     * Although
     */
    fun activeShizukuUserService() {
        viewModelScope.launch {
            activeShizukuUserServiceUseCase()
                .onSuccess { _endpointActived.value = true }
                .onFailure { error -> _shizukuEvent.emit(PluginError(errorMessage = error.message!!, errorCause = error.stackTrace.OutOfStringLike())) }
        }
    }
}