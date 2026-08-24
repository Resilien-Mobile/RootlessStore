package com.baidaidai.rootless_store.domain.setting.usecase

import com.baidaidai.rootless_store.data.setting.repository.SettingPreferenceRepositoryImpl
import com.baidaidai.rootless_store.domain.setting.model.SettingScreenPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSettingScreenPreferencesUseCase @Inject constructor(
    private val settingPreferencesRepositoryImpl: SettingPreferenceRepositoryImpl
) {
    operator fun invoke(): Flow<SettingScreenPreferences> =
        settingPreferencesRepositoryImpl.observeSettingScreenPreferences()
}
