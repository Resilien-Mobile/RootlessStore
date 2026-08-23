package com.baidaidai.rootless_store.data.update.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.update.remote.api.GitHubReleaseApi
import com.baidaidai.rootless_store.data.update.remote.dto.GitHubReleaseDto
import io.ktor.client.call.body
import javax.inject.Inject

class StoreUpdateGatewayImpl @Inject constructor(
    private val gitHubReleaseApi: GitHubReleaseApi
){
    suspend fun fetchLatestVersion(): String? {
        try{
            val httpResponse = gitHubReleaseApi.fetchLatestVersionTagName()
            val gitHubReleaseDto = httpResponse.body<GitHubReleaseDto>()  // Automatically ignore the useless values


            Log.d("StoreUpdateGatewayImpl.fetchLatestVersion",gitHubReleaseDto.tag_name)
            return gitHubReleaseDto.tag_name
        }catch (error: Throwable){
            // It's Useless to show error message for users
            Log.d("StoreUpdateGatewayImpl.fetchLatestVersion","Connection Time Out")
            return null
        }
    }
}
