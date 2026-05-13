package com.baidaidai.rootless_store.domain.source.model

interface PluginSourceAuthenticationResult {
    data class Success(
        val userName: String,
        val userAccessToken: String
    ) : PluginSourceAuthenticationResult

    data class AccessDenied(
        val httpStatusCode: Int,
        val errorMessage: String
    ) : PluginSourceAuthenticationResult

    data object NetworkError : PluginSourceAuthenticationResult

    data object ServerError: PluginSourceAuthenticationResult
}