package com.baidaidai.rootless_store.data.market.remote.datasource

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class MarketPackageRemoteDataSource @Inject constructor(
    ktorClient: HttpClient
){
    private val client = ktorClient

    suspend fun fetchPackage(packageUrl: String): HttpResponse {
        return client.get(
            urlString = packageUrl
        )
    }
}
