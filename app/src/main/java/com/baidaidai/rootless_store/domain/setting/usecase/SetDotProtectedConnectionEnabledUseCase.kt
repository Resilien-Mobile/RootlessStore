package com.baidaidai.rootless_store.domain.setting.usecase

import com.baidaidai.rootless_store.data.setting.repository.SettingPreferencesRepositoryImpl
import javax.inject.Inject

class SetDotProtectedConnectionEnabledUseCase @Inject constructor(
    private val settingPreferencesRepositoryImpl: SettingPreferencesRepositoryImpl
) {
    suspend operator fun invoke(isEnabled: Boolean) =
        settingPreferencesRepositoryImpl.setDotProtectedConnectionEnabled(isEnabled)
}
