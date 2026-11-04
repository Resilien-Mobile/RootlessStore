package com.baidaidai.rootless_store.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RootlessStorePreferencesDataStoreTest {

    @Test
    fun removesLegacyExecutionContextPreferencesWithoutChangingOtherSettings() = runBlocking {
        val executionContextKey = stringPreferencesKey("execute_context")
        val chooserEnabledKey = booleanPreferencesKey("enable_chooser")
        val otherSettingKey = booleanPreferencesKey("notify_plugin_status")
        val preferences = emptyPreferences().toMutablePreferences().apply {
            this[executionContextKey] = "ADB"
            this[chooserEnabledKey] = true
            this[otherSettingKey] = true
        }

        assertTrue(removeLegacyExecutionContextPreferences.shouldMigrate(preferences))

        val migratedPreferences = removeLegacyExecutionContextPreferences.migrate(preferences)

        assertNull(migratedPreferences[executionContextKey])
        assertNull(migratedPreferences[chooserEnabledKey])
        assertEquals(true, migratedPreferences[otherSettingKey])
        assertFalse(removeLegacyExecutionContextPreferences.shouldMigrate(migratedPreferences))
    }
}
