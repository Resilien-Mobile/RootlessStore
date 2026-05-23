package com.baidaidai.rootless_store.domain.setting.usecase

import com.baidaidai.rootless_store.data.setting.repository.SettingPreferenceRepositoryImpl
import javax.inject.Inject

class SetUseThirdPartyNotificationPushUseCase @Inject constructor(
    private val settingPreferencesRepositoryImpl: SettingPreferenceRepositoryImpl
) {
    suspend operator fun invoke(enabled: Boolean) =
        settingPreferencesRepositoryImpl.setUseThirdPartyNotificationPush(enabled)
}
