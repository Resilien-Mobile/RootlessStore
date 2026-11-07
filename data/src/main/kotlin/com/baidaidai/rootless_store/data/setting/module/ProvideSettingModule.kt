package com.baidaidai.rootless_store.data.setting.module

import com.baidaidai.rootless_store.data.setting.repository.SettingPreferencesRepositoryImpl
import com.baidaidai.rootless_store.domain.setting.repository.SettingPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideSettingModule {

    @Binds
    abstract fun ProvideSettingPreferencesRepository(
        settingPreferencesRepositoryImpl: SettingPreferencesRepositoryImpl
    ): SettingPreferencesRepository

}
