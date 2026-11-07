package com.baidaidai.rootless_store.data.shizuku.module

import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionGatewayImpl
import com.baidaidai.rootless_store.domain.shizuku.gateway.ShizukuPermissionGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideShizukuGatewayModule {

    @Binds
    abstract fun ProvideShizukuPermissionGateway(
        shizukuPermissionGatewayImpl: ShizukuPermissionGatewayImpl
    ): ShizukuPermissionGateway

}
