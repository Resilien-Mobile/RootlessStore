package com.baidaidai.rootless_store.data.update.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class GithubReleaseAPI @Inject constructor(
    private val ktorClient: HttpClient
) {
    private val client = ktorClient

    suspend fun getLatestVersionTagName(): HttpResponse{
        return client.request(
            urlString = "https://api.github.com/repos/Resilien-Mobile/RootlessStore/releases/latest"
        ){
            header("Accept", "application/vnd.github+json")
            header("User-Agent", "RootlessStore-App")
        }
    }
}