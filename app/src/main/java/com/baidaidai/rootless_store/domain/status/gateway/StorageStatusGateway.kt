package com.baidaidai.rootless_store.domain.status.gateway

import com.baidaidai.rootless_store.domain.status.model.StorageStatus

interface StorageStatusGateway{
    fun getStorageStatus(): StorageStatus
}