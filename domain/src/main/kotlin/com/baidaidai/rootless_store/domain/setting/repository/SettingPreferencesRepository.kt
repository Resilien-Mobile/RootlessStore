package com.baidaidai.rootless_store.domain.setting.repository

import com.baidaidai.rootless_store.domain.setting.model.SettingScreenPreferences
import kotlinx.coroutines.flow.Flow

interface SettingPreferencesRepository {

    fun observeSettingScreenPreferences(): Flow<SettingScreenPreferences>

    fun observeAutoUpdateEnabledPreference(): Flow<Boolean>

    fun observePluginStatusNotificationEnabled(): Flow<Boolean>

    suspend fun setPluginStatusNotificationEnabled(
        isEnabled: Boolean
    )

    suspend fun setThirdPartyNotificationPushEnabled(
        isEnabled: Boolean
    )

    suspend fun setInsecureConnectionAllowed(
        isAllowed: Boolean
    )

    suspend fun setDotProtectedConnectionEnabled(
        isEnabled: Boolean
    )

    suspend fun setAutoUpdateEnabled(
        isEnabled: Boolean
    )

    suspend fun enableDeveloperOptions()

}
