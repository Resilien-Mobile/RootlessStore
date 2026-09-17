package com.baidaidai.rootless_store.data.codebrick.module

import android.content.Context
import com.baidaidai.rootless_store.data.codebrick.datasource.CodeBrickDataSourceImpl
import com.baidaidai.rootless_store.domain.codebrick.gateway.CodeBrickDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CodeBrickDataSourceModule {

    @Provides
    @Singleton
    fun provideCodeBrickDataSource(
        @ApplicationContext context: Context
    ): CodeBrickDataSource {
        return CodeBrickDataSourceImpl(context)
    }

}
