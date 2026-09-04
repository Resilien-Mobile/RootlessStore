package com.baidaidai.rootless_store.domain.market.error

import com.baidaidai.rootless_store.domain.error.RootlessStoreError

data class MarketError(
    override val errorMessage: String,
    override val errorCause: String
): RootlessStoreError
