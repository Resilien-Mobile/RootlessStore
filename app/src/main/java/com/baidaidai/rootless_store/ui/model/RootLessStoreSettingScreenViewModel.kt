package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.setting.model.SettingScreenPreference
import com.baidaidai.rootless_store.domain.setting.usecase.GetSettingScreenPreferencesUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.SetAllowInsecureConnectionUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.SetNotifyPluginStatusUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.SetUseDotProtectedConnectionUseCase
import com.baidaidai.rootless_store.domain.setting.usecase.SetUseThirdPartyNotificationPushUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootLessStoreSettingScreenViewModel @Inject constructor(
    getSettingPanelPreferencesUseCase: GetSettingScreenPreferencesUseCase,
    private val setNotifyPluginStatusUseCase: SetNotifyPluginStatusUseCase,
    private val setUseThirdPartyNotificationPushUseCase: SetUseThirdPartyNotificationPushUseCase,
    private val setAllowInsecureConnectionUseCase: SetAllowInsecureConnectionUseCase,
    private val setUseDotProtectedConnectionUseCase: SetUseDotProtectedConnectionUseCase
) : ViewModel() {

    val settingPanelPreferences: StateFlow<SettingScreenPreference> =
        getSettingPanelPreferencesUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1_000),
            initialValue = SettingScreenPreference()
        )

    fun setNotifyPluginStatus(enabled: Boolean) {
        viewModelScope.launch {
            setNotifyPluginStatusUseCase(enabled)
        }
    }

    fun setUseThirdPartyNotificationPush(enabled: Boolean) {
        viewModelScope.launch {
            setUseThirdPartyNotificationPushUseCase(enabled)
        }
    }

    fun setAllowInsecureConnection(enabled: Boolean) {
        viewModelScope.launch {
            setAllowInsecureConnectionUseCase(enabled)
        }
    }

    fun setUseDotProtectedConnection(enabled: Boolean) {
        viewModelScope.launch {
            setUseDotProtectedConnectionUseCase(enabled)
        }
    }
}
