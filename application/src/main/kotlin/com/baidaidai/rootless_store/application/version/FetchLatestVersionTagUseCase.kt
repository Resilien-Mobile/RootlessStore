package com.baidaidai.rootless_store.application.version

import com.baidaidai.rootless_store.domain.update.gateway.StoreUpdateGateway
import javax.inject.Inject

class FetchLatestVersionTagUseCase @Inject constructor(
    private val storeUpdateGateway: StoreUpdateGateway,
) {
    suspend operator fun invoke(): Result<String> {

        return storeUpdateGateway.fetchLatestVersionTag()

    }
}
