package com.baidaidai.rootless_store.application.setting

import com.baidaidai.rootless_store.domain.setting.repository.SettingPreferencesRepository
import javax.inject.Inject

class SetAutoUpdateEnabledUseCase @Inject constructor(
    private val settingPreferencesRepository: SettingPreferencesRepository
) {
    suspend operator fun invoke(isEnabled: Boolean) =
        settingPreferencesRepository.setAutoUpdateEnabled(isEnabled)
}
