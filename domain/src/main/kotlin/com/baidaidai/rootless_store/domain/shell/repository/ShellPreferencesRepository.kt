package com.baidaidai.rootless_store.domain.shell.repository

import com.baidaidai.rootless_store.domain.shell.model.ShellContextPreferences
import kotlinx.coroutines.flow.Flow

interface ShellPreferencesRepository {

    fun observeShellContextPreferences(): Flow<ShellContextPreferences>

    suspend fun setDirectoryJumpEnabled(
        isEnabled: Boolean
    )

}
