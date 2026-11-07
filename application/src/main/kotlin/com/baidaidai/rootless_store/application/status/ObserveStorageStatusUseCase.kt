package com.baidaidai.rootless_store.application.status

import com.baidaidai.rootless_store.domain.status.repository.StoreStatusRepository
import com.baidaidai.rootless_store.domain.status.model.StorageStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveStorageStatusUseCase @Inject constructor(
    private val storeStatusRepository: StoreStatusRepository
){
    operator fun invoke(): Flow<StorageStatus> {
        return storeStatusRepository.observeStorageStatus()
    }
}
