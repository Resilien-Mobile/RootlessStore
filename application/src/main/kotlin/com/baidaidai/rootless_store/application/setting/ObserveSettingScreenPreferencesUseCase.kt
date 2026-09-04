package com.baidaidai.rootless_store.application.setting

import com.baidaidai.rootless_store.data.setting.repository.SettingPreferencesRepositoryImpl
import com.baidaidai.rootless_store.domain.setting.model.SettingScreenPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSettingScreenPreferencesUseCase @Inject constructor(
    private val settingPreferencesRepositoryImpl: SettingPreferencesRepositoryImpl
) {
    operator fun invoke(): Flow<SettingScreenPreferences> =
        settingPreferencesRepositoryImpl.observeSettingScreenPreferences()
}