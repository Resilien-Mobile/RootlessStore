package com.baidaidai.rootless_store.data.assistant.module

import com.baidaidai.rootless_store.data.assistant.model.AssistantApi
import com.baidaidai.rootless_store.data.assistant.model.EngineManager
import com.baidaidai.rootless_store.data.assistant.api.LocallyLiteRTApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideAssistantApiModule {

    @Binds
    @Singleton
    abstract fun ProvideAssistantApi(
        implementation: LocallyLiteRTApi
    ): AssistantApi

    @Binds
    @Singleton
    abstract fun ProvideEngineManager(
        implementation: LocallyLiteRTApi
    ): EngineManager

}
