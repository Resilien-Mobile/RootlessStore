package com.baidaidai.rootless_store.application.setting

import com.baidaidai.rootless_store.data.setting.repository.SettingPreferencesRepositoryImpl
import javax.inject.Inject

class SetThirdPartyNotificationPushEnabledUseCase @Inject constructor(
    private val settingPreferencesRepositoryImpl: SettingPreferencesRepositoryImpl
) {
    suspend operator fun invoke(isEnabled: Boolean) =
        settingPreferencesRepositoryImpl.setThirdPartyNotificationPushEnabled(isEnabled)
}
