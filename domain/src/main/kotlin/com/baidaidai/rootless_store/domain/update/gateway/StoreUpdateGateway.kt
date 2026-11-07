package com.baidaidai.rootless_store.domain.update.gateway

interface StoreUpdateGateway {

    suspend fun fetchLatestVersionTag(): Result<String>

}
