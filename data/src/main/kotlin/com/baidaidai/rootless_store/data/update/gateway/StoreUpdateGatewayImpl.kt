package com.baidaidai.rootless_store.data.update.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.update.remote.api.GithubReleaseApi
import com.baidaidai.rootless_store.data.update.remote.dto.GithubReleaseDto
import io.ktor.client.call.body
import javax.inject.Inject

class StoreUpdateGatewayImpl @Inject constructor(
    private val githubReleaseApi: GithubReleaseApi
){
    suspend fun fetchLatestVersionTag(): String? {
        try{
            val httpResponse = githubReleaseApi.fetchLatestVersionTagName()
            val githubReleaseDto = httpResponse.body<GithubReleaseDto>()  // Automatically ignore the useless values


            Log.d("StoreUpdateGatewayImpl.fetchLatestVersionTag",githubReleaseDto.tagName)
            return githubReleaseDto.tagName
        }catch (error: Throwable){
            // It's Useless to show error message for users
            Log.d("StoreUpdateGatewayImpl.fetchLatestVersionTag","Connection Time Out")
            return null
        }
    }
}
