package com.baidaidai.rootless_store.domain.setting.usecase

import com.baidaidai.rootless_store.data.setting.repository.SettingPreferenceRepositoryImpl
import javax.inject.Inject

class SetAutoUpdateEnabledUseCase @Inject constructor(
    private val settingPreferencesRepositoryImpl: SettingPreferenceRepositoryImpl
) {
    suspend operator fun invoke(isEnabled: Boolean) =
        settingPreferencesRepositoryImpl.setAutoUpdateEnabled(isEnabled)
}