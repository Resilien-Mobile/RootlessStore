package com.baidaidai.rootless_store.domain.fileSystem.gateway

interface AndroidFileSystemSearchOperatorGateway {

    fun hasPluginDirectory(): Boolean

    fun hasPluginCacheDirectory(): Boolean

    fun hasEnvironmentDirectory(): Boolean

    fun hasEnvironmentCacheDirectory(): Boolean

}
