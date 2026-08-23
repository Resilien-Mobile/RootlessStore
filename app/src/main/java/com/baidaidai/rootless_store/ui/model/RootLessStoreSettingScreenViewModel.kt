package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.setting.model.SettingScreenPreference
import com.baidaidai.rootless_store.domain.setting.usecase.ObserveSettingScreenPreferencesUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.SetInsecureConnectionAllowedUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.SetAutoUpdateEnabledUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.SetPluginStatusNotificationEnabledUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.SetDotProtectedConnectionEnabledUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.SetThirdPartyNotificationPushEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootLessStoreSettingScreenViewModel @Inject constructor(
    observeSettingScreenPreferencesUseCase: ObserveSettingScreenPreferencesUseCase,
    private val setPluginStatusNotificationEnabledUseCase: SetPluginStatusNotificationEnabledUseCase,
    private val setThirdPartyNotificationPushEnabledUseCase: SetThirdPartyNotificationPushEnabledUseCase,
    private val setInsecureConnectionAllowedUseCase: SetInsecureConnectionAllowedUseCase,
    private val setDotProtectedConnectionEnabledUseCase: SetDotProtectedConnectionEnabledUseCase,
    private val setAutoUpdateEnabledUseCase: SetAutoUpdateEnabledUseCase,
) : ViewModel() {

    val settingPanelPreferences: StateFlow<SettingScreenPreference> =
        observeSettingScreenPreferencesUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1_000),
            initialValue = SettingScreenPreference()
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
