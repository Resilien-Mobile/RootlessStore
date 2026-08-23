package com.baidaidai.rootless_store.data.plugin.remote.datasource

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class ModulePackageRemoteDataSource @Inject constructor(
    ktorClient: HttpClient
){
    private val client = ktorClient

    suspend fun fetchPackage(packageUri: String): HttpResponse {
        return client.get(
            urlString = packageUri
        )
    }
}
