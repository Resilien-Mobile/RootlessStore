package com.baidaidai.rootless_store.data.shell.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.baidaidai.rootless_store.core.datastore.rootlessStorePreferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

data class ShellContextPreferences(
    val enableRunAs: Boolean = false,
    val jumpToDirectory: Boolean = false
)

class ShellPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val shellContextPreferences: Flow<ShellContextPreferences> =
        context.rootlessStorePreferencesDataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { preferences ->
                ShellContextPreferences(
                    enableRunAs = preferences[ENABLE_RUN_AS] ?: false,
                    jumpToDirectory = preferences[JUMP_TO_DIRECTORY] ?: false
                )
            }

    suspend fun setEnableRunAs(enabled: Boolean) {
        context.rootlessStorePreferencesDataStore.edit { preferences ->
            preferences[ENABLE_RUN_AS] = enabled
        }
    }

    suspend fun setJumpToDirectory(enabled: Boolean) {
        context.rootlessStorePreferencesDataStore.edit { preferences ->
            preferences[JUMP_TO_DIRECTORY] = enabled
        }
    }

    private companion object {
        val ENABLE_RUN_AS = booleanPreferencesKey("shell_enable_run_as")
        val JUMP_TO_DIRECTORY = booleanPreferencesKey("shell_jump_to_directory")
    }
}
