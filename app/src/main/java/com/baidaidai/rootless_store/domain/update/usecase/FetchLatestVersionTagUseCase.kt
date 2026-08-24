package com.baidaidai.rootless_store.domain.update.usecase

import com.baidaidai.rootless_store.data.update.repository.StoreUpdateRepositoryImpl
import javax.inject.Inject

class FetchLatestVersionTagUseCase @Inject constructor(
    private val storeUpdateRepositoryImpl: StoreUpdateRepositoryImpl
) {
    suspend operator fun invoke(): String? = storeUpdateRepositoryImpl.fetchLatestVersionTag()
}
