package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.notification.usecase.AddOneNotificationPreferenceUseCase
import com.baidaidai.rootless_store.domain.notification.usecase.GetOneNotificationPreferenceUseCase
import com.baidaidai.rootless_store.ui.uistate.RootLessStoreThirdPartyNotificationScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootLessStoreThirdPartyNotificationScreenViewModel @Inject constructor(
    private val addOneNotificationPreferenceUseCase: AddOneNotificationPreferenceUseCase,
    private val getOneNotificationPreferenceUseCase: GetOneNotificationPreferenceUseCase
) : ViewModel() {

    init {
        getNotificationPreference()
    }

    private val _thirdPartyNotificationScreenUiState = MutableStateFlow(RootLessStoreThirdPartyNotificationScreenUiState())
    val thirdPartyNotificationScreenUiState = _thirdPartyNotificationScreenUiState.asStateFlow()

    fun onBarkApiKeyChanged(barkApiKey: String){
        _thirdPartyNotificationScreenUiState.update {
            it.copy(barkApiKey = barkApiKey)
        }
    }

    fun onWarningNotificationEnabledChanged(enabled: Boolean){
        _thirdPartyNotificationScreenUiState.update {
            it.copy(warningNotificationEnabled = enabled)
        }
    }

    fun onSubmitClick(){
        viewModelScope.launch {
            val uiState = thirdPartyNotificationScreenUiState.value

            addOneNotificationPreferenceUseCase(
                barkApiKey = uiState.barkApiKey,
                warningNotificationEnabled = uiState.warningNotificationEnabled
            )
        }
    }

    private fun getNotificationPreference(){
        viewModelScope.launch {
            val notificationPreference = getOneNotificationPreferenceUseCase() ?: return@launch

            _thirdPartyNotificationScreenUiState.update {
                it.copy(
                    barkApiKey = notificationPreference.apiKey,
                    warningNotificationEnabled = notificationPreference.criticalWarning
                )
            }
        }
    }

}