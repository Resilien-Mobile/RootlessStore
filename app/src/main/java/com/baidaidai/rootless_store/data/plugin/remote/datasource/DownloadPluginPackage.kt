package com.baidaidai.rootless_store.data.plugin.remote.datasource

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class DownloadPluginPackage @Inject constructor(
    ktorClient: HttpClient
){
    private val client = ktorClient

    suspend fun usePluginUri(pluginUri: String): HttpResponse {
        return client.get(
            urlString = pluginUri
        )
    }
}