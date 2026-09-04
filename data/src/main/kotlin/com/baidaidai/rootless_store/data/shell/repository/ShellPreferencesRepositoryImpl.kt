package com.baidaidai.rootless_store.data.shell.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.baidaidai.rootless_store.data.datastore.rootlessStorePreferencesDataStore
import com.baidaidai.rootless_store.domain.shell.model.ShellContextPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


class ShellPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun observeShellContextPreferences(): Flow<ShellContextPreferences> =
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
                    shouldJumpToDirectory = preferences[SHOULD_JUMP_TO_DIRECTORY] ?: false
                )
            }

    suspend fun setDirectoryJumpEnabled(isEnabled: Boolean) {
        context.rootlessStorePreferencesDataStore.edit { preferences ->
            preferences[SHOULD_JUMP_TO_DIRECTORY] = isEnabled
        }
    }

    private companion object {
        val SHOULD_JUMP_TO_DIRECTORY = booleanPreferencesKey("shell_jump_to_directory")
    }
}
