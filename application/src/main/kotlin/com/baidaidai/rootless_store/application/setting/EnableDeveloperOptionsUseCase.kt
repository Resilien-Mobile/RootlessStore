package com.baidaidai.rootless_store.application.setting

import com.baidaidai.rootless_store.domain.setting.repository.SettingPreferencesRepository
import javax.inject.Inject

class EnableDeveloperOptionsUseCase @Inject constructor(
    private val settingPreferencesRepository: SettingPreferencesRepository
) {
    suspend operator fun invoke() {
        settingPreferencesRepository.enableDeveloperOptions()
    }
}