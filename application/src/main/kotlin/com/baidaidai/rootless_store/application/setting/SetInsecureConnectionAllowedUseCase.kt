package com.baidaidai.rootless_store.application.setting

import com.baidaidai.rootless_store.domain.setting.repository.SettingPreferencesRepository
import javax.inject.Inject

class SetInsecureConnectionAllowedUseCase @Inject constructor(
    private val settingPreferencesRepository: SettingPreferencesRepository
) {
    suspend operator fun invoke(isAllowed: Boolean) =
        settingPreferencesRepository.setInsecureConnectionAllowed(isAllowed)
}
