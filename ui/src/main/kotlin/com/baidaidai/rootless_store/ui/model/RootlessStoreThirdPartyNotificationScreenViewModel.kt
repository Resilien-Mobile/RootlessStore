package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.notification.EnsureNotificationPreferenceUseCase
import com.baidaidai.rootless_store.application.notification.FindNotificationPreferenceUseCase
import com.baidaidai.rootless_store.ui.uistate.RootlessStoreThirdPartyNotificationScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootlessStoreThirdPartyNotificationScreenViewModel @Inject constructor(
    private val ensureNotificationPreferenceUseCase: EnsureNotificationPreferenceUseCase,
    private val findNotificationPreferenceUseCase: FindNotificationPreferenceUseCase
) : ViewModel() {

    init {
        loadNotificationPreference()
    }

    private val _thirdPartyNotificationScreenUiState = MutableStateFlow(RootlessStoreThirdPartyNotificationScreenUiState())
    val thirdPartyNotificationScreenUiState = _thirdPartyNotificationScreenUiState.asStateFlow()


    // Ui Event cluster
    // Change UiState methods
    fun setBarkApiKey(barkApiKey: String){
        _thirdPartyNotificationScreenUiState.update {
            it.copy(barkApiKey = barkApiKey)
        }
    }
    fun setNotificationTitle(notificationTitle: String?){
        _thirdPartyNotificationScreenUiState.update {
            it.copy(notificationTitle = notificationTitle)
        }
    }
    fun setSelfBuiltServer(selfBuiltServer: String?){
        _thirdPartyNotificationScreenUiState.update {
            it.copy(selfBuiltServer = selfBuiltServer)
        }
    }
    fun setWarningNotificationEnabled(isEnabled: Boolean){
        _thirdPartyNotificationScreenUiState.update {
            it.copy(isWarningNotificationEnabled = isEnabled)
        }
    }

    fun ensureNotificationPreference(){
        viewModelScope.launch {
            val uiState = thirdPartyNotificationScreenUiState.value

            ensureNotificationPreferenceUseCase(
                barkApiKey = uiState.barkApiKey,
                notificationTitle = uiState.notificationTitle,
                selfBuiltServer = uiState.selfBuiltServer,
                isWarningNotificationEnabled = uiState.isWarningNotificationEnabled
            )
        }
    }

    private fun loadNotificationPreference(){
        viewModelScope.launch {
            val notificationPreference = findNotificationPreferenceUseCase() ?: return@launch

            _thirdPartyNotificationScreenUiState.update {
                it.copy(
                    barkApiKey = notificationPreference.apiKey,
                    isWarningNotificationEnabled = notificationPreference.isCriticalWarningEnabled
                )
            }
        }
    }

}
