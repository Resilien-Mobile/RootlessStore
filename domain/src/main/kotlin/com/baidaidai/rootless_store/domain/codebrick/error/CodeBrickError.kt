package com.baidaidai.rootless_store.domain.codebrick.error

import com.baidaidai.rootless_store.domain.error.RootlessStoreError

data class CodeBrickError(
    override val errorCause: String,
    override val errorMessage: String
): RootlessStoreError