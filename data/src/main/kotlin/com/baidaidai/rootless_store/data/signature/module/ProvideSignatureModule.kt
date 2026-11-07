package com.baidaidai.rootless_store.data.signature.module

import com.baidaidai.rootless_store.data.signature.gateway.PackageSignatureGatewayImpl
import com.baidaidai.rootless_store.data.signature.repository.CaCertificateRepositoryImpl
import com.baidaidai.rootless_store.domain.signature.gateway.PackageSignatureGateway
import com.baidaidai.rootless_store.domain.signature.repository.CaCertificateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideSignatureModule {

    @Binds
    abstract fun ProvidePackageSignatureGateway(
        packageSignatureGatewayImpl: PackageSignatureGatewayImpl
    ): PackageSignatureGateway

    @Binds
    abstract fun ProvideCaCertificateRepository(
        caCertificateRepositoryImpl: CaCertificateRepositoryImpl
    ): CaCertificateRepository

}
