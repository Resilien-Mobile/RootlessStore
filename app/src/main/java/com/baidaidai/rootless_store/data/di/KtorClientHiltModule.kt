package com.baidaidai.rootless_store.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestRemote
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import com.baidaidai.rootless_store.domain.plugin.manifest.RootlessStoreManifestCollection
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KtorClientHiltModule {
    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient {
        return HttpClient(Android){
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        explicitNulls = false
                        classDiscriminator = "type"
                        serializersModule = SerializersModule {
                            polymorphic(RootlessStoreManifestCollection::class) {
                                subclass(PluginManifestRemote::class)
                                subclass(EnvironmentManifestRemote::class)
                            }
                        }
                    }
                )
            }
        }
    }
}