package com.baidaidai.rootless_store.domain.execution.error

import com.baidaidai.rootless_store.domain.error.RootlessStoreError

data class ExecutionError(
    override val errorMessage: String,
    override val errorCause: String
): RootlessStoreError