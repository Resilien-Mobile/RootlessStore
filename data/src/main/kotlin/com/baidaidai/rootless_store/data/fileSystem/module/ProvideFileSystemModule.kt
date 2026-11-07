package com.baidaidai.rootless_store.data.fileSystem.module

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemChmodOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDeleteOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemRezipOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemSearchOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemShareOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemUnzipOperatorGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemChmodOperatorGateway
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCreateOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemDefaultOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemDeleteOperatorGateway
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemReadOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemRezipOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemSearchOperatorGateway
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemShareOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemUnzipOperatorGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideFileSystemModule {

    @Binds
    abstract fun ProvideAndroidFileSystemCapabilityGateway(
        implementation: AndroidFileSystemCapabilityGatewayImpl
    ): AndroidFileSystemCapabilityGateway

    @Binds
    abstract fun ProvideAndroidFileSystemChmodOperatorGateway(
        implementation: AndroidFileSystemChmodOperatorGatewayImpl
    ): AndroidFileSystemChmodOperatorGateway

    @Binds
    abstract fun ProvideAndroidFileSystemCreateOperatorGateway(
        implementation: AndroidFileSystemCreateOperatorGatewayImpl
    ): AndroidFileSystemCreateOperatorGateway

    @Binds
    abstract fun ProvideAndroidFileSystemDefaultOperatorGateway(
        implementation: AndroidFileSystemDefaultOperatorGatewayImpl
    ): AndroidFileSystemDefaultOperatorGateway

    @Binds
    abstract fun ProvideAndroidFileSystemDeleteOperatorGateway(
        implementation: AndroidFileSystemDeleteOperatorGatewayImpl
    ): AndroidFileSystemDeleteOperatorGateway

    @Binds
    abstract fun ProvideAndroidFileSystemReadOperatorGateway(
        implementation: AndroidFileSystemReadOperatorGatewayImpl
    ): AndroidFileSystemReadOperatorGateway

    @Binds
    abstract fun ProvideAndroidFileSystemRezipOperatorGateway(
        implementation: AndroidFileSystemRezipOperatorGatewayImpl
    ): AndroidFileSystemRezipOperatorGateway

    @Binds
    abstract fun ProvideAndroidFileSystemSearchOperatorGateway(
        implementation: AndroidFileSystemSearchOperatorGatewayImpl
    ): AndroidFileSystemSearchOperatorGateway

    @Binds
    abstract fun ProvideAndroidFileSystemShareOperatorGateway(
        implementation: AndroidFileSystemShareOperatorGatewayImpl
    ): AndroidFileSystemShareOperatorGateway

    @Binds
    abstract fun ProvideAndroidFileSystemUnzipOperatorGateway(
        implementation: AndroidFileSystemUnzipOperatorGatewayImpl
    ): AndroidFileSystemUnzipOperatorGateway

}
