package com.baidaidai.rootless_store.data.datastore

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

internal val removeLegacyExecutionContextPreferences = object : DataMigration<Preferences> {
    private val executionContextKey = stringPreferencesKey("execute_context")
    private val chooserEnabledKey = booleanPreferencesKey("enable_chooser")

    override suspend fun shouldMigrate(currentData: Preferences): Boolean =
        currentData[executionContextKey] != null || currentData[chooserEnabledKey] != null

    override suspend fun migrate(currentData: Preferences): Preferences =
        currentData.toMutablePreferences().apply {
            remove(executionContextKey)
            remove(chooserEnabledKey)
        }

    override suspend fun cleanUp() = Unit
}

val Context.rootlessStorePreferencesDataStore by preferencesDataStore(
    name = "rootless_store_preferences",
    produceMigrations = { listOf(removeLegacyExecutionContextPreferences) }
)
