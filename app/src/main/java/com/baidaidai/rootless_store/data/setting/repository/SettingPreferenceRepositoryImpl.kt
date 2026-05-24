package com.baidaidai.rootless_store.data.setting.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.baidaidai.rootless_store.core.datastore.rootlessStorePreferencesDataStore
import com.baidaidai.rootless_store.domain.setting.model.SettingScreenPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


class SettingPreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Read
    val getSettingScreenPreference: Flow<SettingScreenPreference> =
        context.rootlessStorePreferencesDataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { preferences ->
                SettingScreenPreference(
                    enableAutoUpdate = preferences[ENABLE_AUTO_UPDATE] ?: false,
                    notifyPluginStatus = preferences[NOTIFY_PLUGIN_STATUS] ?: false,
                    useThirdPartyNotificationPush = preferences[USE_THIRD_PARTY_NOTIFICATION_PUSH] ?: false,
                    allowInsecureConnection = preferences[ALLOW_INSECURE_CONNECTION] ?: false,
                    useDotProtectedConnection = preferences[USE_DOT_PROTECTED_CONNECTION] ?: false
                )
            }

    fun getEnableAutoUpdatePreference(): Flow<Boolean> =
        context.rootlessStorePreferencesDataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { preferences ->
                preferences[ENABLE_AUTO_UPDATE] ?: false
            }

    // Update
    suspend fun setNotifyPluginStatus(enabled: Boolean) {
        context.rootlessStorePreferencesDataStore.edit { preferences ->
            preferences[NOTIFY_PLUGIN_STATUS] = enabled
        }
    }

    suspend fun setUseThirdPartyNotificationPush(enabled: Boolean) {
        context.rootlessStorePreferencesDataStore.edit { preferences ->
            preferences[USE_THIRD_PARTY_NOTIFICATION_PUSH] = enabled
        }
    }

    suspend fun setAllowInsecureConnection(enabled: Boolean) {
        context.rootlessStorePreferencesDataStore.edit { preferences ->
            preferences[ALLOW_INSECURE_CONNECTION] = enabled
        }
    }

    suspend fun setUseDotProtectedConnection(enabled: Boolean) {
        context.rootlessStorePreferencesDataStore.edit { preferences ->
            preferences[USE_DOT_PROTECTED_CONNECTION] = enabled
        }
    }

    suspend fun changeEnableAutoUpdate(enabled: Boolean) {
        context.rootlessStorePreferencesDataStore.edit { preferences ->
            preferences[ENABLE_AUTO_UPDATE] = enabled
        }
    }

    private companion object {
        val NOTIFY_PLUGIN_STATUS = booleanPreferencesKey("setting_notify_plugin_status")
        val USE_THIRD_PARTY_NOTIFICATION_PUSH = booleanPreferencesKey("setting_use_third_party_notification_push")
        val ALLOW_INSECURE_CONNECTION = booleanPreferencesKey("setting_allow_insecure_connection")
        val USE_DOT_PROTECTED_CONNECTION = booleanPreferencesKey("setting_use_dot_protected_connection")
        val ENABLE_AUTO_UPDATE = booleanPreferencesKey("setting_enable_auto_update")
    }
}
