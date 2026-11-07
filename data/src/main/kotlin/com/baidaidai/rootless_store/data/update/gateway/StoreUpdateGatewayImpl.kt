package com.baidaidai.rootless_store.data.update.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.update.remote.api.GithubReleaseApi
import javax.inject.Inject

class StoreUpdateGatewayImpl @Inject constructor(
    private val githubReleaseApi: GithubReleaseApi,
) {
    suspend fun fetchLatestVersionTag(): Result<String> =
        runCatching {
            githubReleaseApi.fetchLatestRelease().tagName
        }
            .onFailure { error ->
                Log.e(
                    "StoreUpdateGatewayImpl.fetchLatestVersionTag",
                    "Failed to fetch latest version tag",
                    error,
                )
            }

}
