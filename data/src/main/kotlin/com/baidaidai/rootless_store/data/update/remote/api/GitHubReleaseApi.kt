package com.baidaidai.rootless_store.data.update.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.request
import com.baidaidai.rootless_store.data.update.remote.dto.GithubReleaseDto
import javax.inject.Inject

class GithubReleaseApi @Inject constructor(
    private val ktorClient: HttpClient
) {
    suspend fun fetchLatestRelease(): GithubReleaseDto {

        val httpResponse = ktorClient.request(
            urlString = "https://api.github.com/repos/Resilien-Mobile/RootlessStore/releases/latest"
        ){
            header(
                key = "Accept",
                value = "application/vnd.github+json",
            )
            header(
                key = "User-Agent",
                value = "RootlessStore-App",
            )
        }

        check(httpResponse.status.value in 200..299) {
            "GitHub release request failed with status ${httpResponse.status}"
        }

        return httpResponse.body<GithubReleaseDto>()

    }
}
