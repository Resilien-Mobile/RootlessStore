package com.baidaidai.rootless_store.application.setting

import com.baidaidai.rootless_store.domain.setting.repository.SettingPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAutoUpdateEnabledPreferenceUseCase @Inject constructor(
    private val settingPreferencesRepository: SettingPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> =
        settingPreferencesRepository.observeAutoUpdateEnabledPreference()
}