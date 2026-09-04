package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMemoryStatusUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl
){
    operator fun invoke(): Flow<MemoryStatus> {
        return storeStatusRepositoryImpl.observeMemoryStatus()
    }
}
