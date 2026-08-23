package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.notification.usecase.AddNotificationPreferenceUseCase
import com.baidaidai.rootless_store.domain.notification.usecase.FindNotificationPreferenceUseCase
import com.baidaidai.rootless_store.ui.uistate.RootLessStoreThirdPartyNotificationScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootLessStoreThirdPartyNotificationScreenViewModel @Inject constructor(
    private val addNotificationPreferenceUseCase: AddNotificationPreferenceUseCase,
    private val findNotificationPreferenceUseCase: FindNotificationPreferenceUseCase
) : ViewModel() {

    init {
        getNotificationPreference()
    }

    private val _thirdPartyNotificationScreenUiState = MutableStateFlow(RootLessStoreThirdPartyNotificationScreenUiState())
    val thirdPartyNotificationScreenUiState = _thirdPartyNotificationScreenUiState.asStateFlow()


    // Ui Event cluster
    // Change UiState methods
    fun onBarkApiKeyChanged(barkApiKey: String){
        _thirdPartyNotificationScreenUiState.update {
            it.copy(barkApiKey = barkApiKey)
        }
    }
    fun onNotificationTitleChanged(notificationTitle: String?){
        _thirdPartyNotificationScreenUiState.update {
            it.copy(notificationTitle = notificationTitle)
        }
    }
    fun onSelfBuiltServerChanged(selfBuiltServer: String?){
        _thirdPartyNotificationScreenUiState.update {
            it.copy(selfBuiltServer = selfBuiltServer)
        }
    }
    fun onWarningNotificationEnabledChanged(isEnabled: Boolean){
        _thirdPartyNotificationScreenUiState.update {
            it.copy(isWarningNotificationEnabled = isEnabled)
        }
    }

    fun onSubmitClick(){
        viewModelScope.launch {
            val uiState = thirdPartyNotificationScreenUiState.value

            addNotificationPreferenceUseCase(
                barkApiKey = uiState.barkApiKey,
                notificationTitle = uiState.notificationTitle,
                selfBuiltServer = uiState.selfBuiltServer,
                isWarningNotificationEnabled = uiState.isWarningNotificationEnabled
            )
        }
    }

    private fun getNotificationPreference(){
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
