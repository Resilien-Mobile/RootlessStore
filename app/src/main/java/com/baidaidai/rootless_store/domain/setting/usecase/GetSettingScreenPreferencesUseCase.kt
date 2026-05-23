package com.baidaidai.rootless_store.domain.setting.usecase

import com.baidaidai.rootless_store.data.setting.repository.SettingPreferenceRepositoryImpl
import com.baidaidai.rootless_store.domain.setting.model.SettingScreenPreference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingScreenPreferencesUseCase @Inject constructor(
    private val settingPreferencesRepositoryImpl: SettingPreferenceRepositoryImpl
) {
    operator fun invoke(): Flow<SettingScreenPreference> =
        settingPreferencesRepositoryImpl.getSettingScreenPreference
}
