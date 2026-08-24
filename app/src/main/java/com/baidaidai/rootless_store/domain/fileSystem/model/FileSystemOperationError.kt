package com.baidaidai.rootless_store.domain.fileSystem.model

import com.baidaidai.rootless_store.domain.error.RootlessStoreError

data class FileSystemOperationError(
    override val errorMessage: String,
    override val errorCause: String
): RootlessStoreError
