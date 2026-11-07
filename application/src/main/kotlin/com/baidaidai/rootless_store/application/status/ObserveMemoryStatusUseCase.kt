package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.domain.status.repository.StoreStatusRepository
import com.baidaidai.rootless_store.domain.status.model.MemoryStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMemoryStatusUseCase @Inject constructor(
    private val storeStatusRepository: StoreStatusRepository
){
    operator fun invoke(): Flow<MemoryStatus> {
        return storeStatusRepository.observeMemoryStatus()
    }
}
