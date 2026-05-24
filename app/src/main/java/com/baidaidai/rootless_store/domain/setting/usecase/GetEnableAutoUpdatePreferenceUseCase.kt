package com.baidaidai.rootless_store.domain.setting.usecase

import com.baidaidai.rootless_store.data.setting.repository.SettingPreferenceRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEnableAutoUpdatePreferenceUseCase @Inject constructor(
    private val settingPreferencesRepositoryImpl: SettingPreferenceRepositoryImpl
) {
    operator fun invoke(): Flow<Boolean> =
        settingPreferencesRepositoryImpl.getEnableAutoUpdatePreference()
}