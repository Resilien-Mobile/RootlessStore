package com.baidaidai.rootless_store.domain.status.gateway

import com.baidaidai.rootless_store.domain.status.model.MemoryStatus

interface MemoryStatusGateway{
    fun getMemoryStatus(): MemoryStatus
}