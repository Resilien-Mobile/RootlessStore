package com.baidaidai.rootless_store.data.shizuku.module

import com.baidaidai.rootless_store.data.shizuku.api.ShizukuApiImpl
import com.baidaidai.rootless_store.domain.shizuku.api.ShizukuApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ShizukuApiModule {

    @Binds
    @Singleton
    abstract fun bindShizukuApi(
        shizukuApiImpl: ShizukuApiImpl,
    ): ShizukuApi

}
