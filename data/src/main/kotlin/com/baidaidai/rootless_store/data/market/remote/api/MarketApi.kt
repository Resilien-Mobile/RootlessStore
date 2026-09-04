package com.baidaidai.rootless_store.data.market.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.appendPathSegments
import javax.inject.Inject
import io.ktor.http.path

class MarketApi @Inject constructor(
    private val ktorClient: HttpClient
){
    private val client = ktorClient

    suspend fun fetchMarketManifests(
        pageNumber: Int,
        pluginSourceEndpoint: String
    ): HttpResponse {
        return client.request(pluginSourceEndpoint){
            method = HttpMethod.Get
            accept(ContentType.Application.Json)
            url{
                appendPathSegments("plugin", "getAllPlugins")
                parameters.append("page", value = pageNumber.toString())
            }
        }
    }
}
