package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.setting.ObserveSettingScreenPreferencesUseCase
import com.baidaidai.rootless_store.application.setting.SetAutoUpdateEnabledUseCase
import com.baidaidai.rootless_store.application.setting.SetDotProtectedConnectionEnabledUseCase
import com.baidaidai.rootless_store.application.setting.SetInsecureConnectionAllowedUseCase
import com.baidaidai.rootless_store.application.setting.SetPluginStatusNotificationEnabledUseCase
import com.baidaidai.rootless_store.application.setting.SetThirdPartyNotificationPushEnabledUseCase
import com.baidaidai.rootless_store.domain.setting.model.SettingScreenPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootlessStoreSettingScreenViewModel @Inject constructor(
    observeSettingScreenPreferencesUseCase: ObserveSettingScreenPreferencesUseCase,
    private val setPluginStatusNotificationEnabledUseCase: SetPluginStatusNotificationEnabledUseCase,
    private val setThirdPartyNotificationPushEnabledUseCase: SetThirdPartyNotificationPushEnabledUseCase,
    private val setInsecureConnectionAllowedUseCase: SetInsecureConnectionAllowedUseCase,
    private val setDotProtectedConnectionEnabledUseCase: SetDotProtectedConnectionEnabledUseCase,
    private val setAutoUpdateEnabledUseCase: SetAutoUpdateEnabledUseCase,
) : ViewModel() {

    val settingScreenPreferences: StateFlow<SettingScreenPreferences> =
        observeSettingScreenPreferencesUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1_000),
            initialValue = SettingScreenPreferences()
        )

    fun setPluginStatusNotificationEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            setPluginStatusNotificationEnabledUseCase(isEnabled)
        }
    }

    fun setThirdPartyNotificationPushEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            setThirdPartyNotificationPushEnabledUseCase(isEnabled)
        }
    }

    fun setInsecureConnectionAllowed(isAllowed: Boolean) {
        viewModelScope.launch {
            setInsecureConnectionAllowedUseCase(isAllowed)
        }
    }

    fun setDotProtectedConnectionEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            setDotProtectedConnectionEnabledUseCase(isEnabled)
        }
    }

    fun setAutoUpdateEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            setAutoUpdateEnabledUseCase(isEnabled)
        }
    }
}
