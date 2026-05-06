package com.baidaidai.rootless_store.core.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.rootlessStorePreferencesDataStore by preferencesDataStore(
    name = "rootless_store_preferences"
)
