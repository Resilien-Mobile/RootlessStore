package com.baidaidai.rootless_store.data.notification.module

import com.baidaidai.rootless_store.data.notification.gateway.NotificationGatewayImpl
import com.baidaidai.rootless_store.data.notification.repository.NotificationPreferenceRepositoryImpl
import com.baidaidai.rootless_store.domain.notification.gateway.NotificationGateway
import com.baidaidai.rootless_store.domain.notification.repository.NotificationPreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideNotificationModule {

    @Binds
    abstract fun ProvideNotificationGateway(
        notificationGatewayImpl: NotificationGatewayImpl
    ): NotificationGateway

    @Binds
    abstract fun ProvideNotificationPreferenceRepository(
        notificationPreferenceRepositoryImpl: NotificationPreferenceRepositoryImpl
    ): NotificationPreferenceRepository

}
