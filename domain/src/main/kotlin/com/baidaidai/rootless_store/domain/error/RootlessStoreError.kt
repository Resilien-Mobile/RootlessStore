package com.baidaidai.rootless_store.domain.error

interface RootlessStoreError {
    val errorMessage: String
    val errorCause: String
}