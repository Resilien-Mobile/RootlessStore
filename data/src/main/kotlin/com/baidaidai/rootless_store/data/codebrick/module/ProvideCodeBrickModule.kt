package com.baidaidai.rootless_store.data.codebrick.module

import com.baidaidai.rootless_store.data.codebrick.gateway.CodeBrickGatewayImpl
import com.baidaidai.rootless_store.data.codebrick.repository.CodeBrickRepositoryImpl
import com.baidaidai.rootless_store.domain.codebrick.gateway.CodeBrickGateway
import com.baidaidai.rootless_store.domain.codebrick.repository.CodeBrickRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideCodeBrickModule {

    @Binds
    abstract fun ProvideCodeBrickGateway(
        codeBrickGatewayImpl: CodeBrickGatewayImpl
    ): CodeBrickGateway

    @Binds
    abstract fun ProvideCodeBrickRepository(
        codeBrickRepositoryImpl: CodeBrickRepositoryImpl
    ): CodeBrickRepository

}
