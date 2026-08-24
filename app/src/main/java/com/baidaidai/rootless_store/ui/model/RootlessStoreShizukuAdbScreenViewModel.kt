package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.shizuku.StartShizukuUserServiceUseCase
import com.baidaidai.rootless_store.application.shizuku.EnsureShizukuPermissionUseCase
import com.baidaidai.rootless_store.core.util.formatAsMultilineString
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
    private val startShizukuUserServiceUseCase: StartShizukuUserServiceUseCase,
    private val ensureShizukuPermissionUseCase: EnsureShizukuPermissionUseCase
): ViewModel() {

    private val _isShizukuActive = MutableStateFlow(false)
    private val _isEndpointActive = MutableStateFlow(false)
    val isShizukuActive = _isShizukuActive.asStateFlow()
    val isEndpointActive = _isEndpointActive.asStateFlow()

    private val _shizukuError = MutableSharedFlow<PluginError?>()
    val shizukuError = _shizukuError.asSharedFlow()


    fun dismissShizukuError() = viewModelScope.launch {
        _shizukuError.emit(null)
    }

    fun ensureShizukuPermission() {
        viewModelScope.launch {
            ensureShizukuPermissionUseCase()
                .onSuccess { isShizukuActive -> _isShizukuActive.value = isShizukuActive }
                .onFailure { error -> _shizukuError.emit(PluginError(errorMessage = error.message!!, errorCause = error.stackTrace.formatAsMultilineString())) }
        }
    }

    /** Starts and connects to the Shizuku user service. */
    fun startShizukuUserService() {
        viewModelScope.launch {
            startShizukuUserServiceUseCase()
                .onSuccess { _isEndpointActive.value = true }
                .onFailure { error -> _shizukuError.emit(PluginError(errorMessage = error.message!!, errorCause = error.stackTrace.formatAsMultilineString())) }
        }
    }
}
