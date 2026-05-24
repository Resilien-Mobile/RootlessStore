package com.baidaidai.rootless_store.data.update.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.update.remote.api.GithubReleaseAPI
import com.baidaidai.rootless_store.data.update.remote.dto.GithubReleaseDTO
import io.ktor.client.call.body
import javax.inject.Inject

class StoreUpdateGatewayImpl @Inject constructor(
    private val githubReleaseAPI: GithubReleaseAPI
){
    suspend fun getLatestVersion(): String? {
        try{
            val httpResponse = githubReleaseAPI.getLatestVersionTagName()
            val githubReleaseDTO = httpResponse.body<GithubReleaseDTO>()  // Automatically ignore the useless values


            Log.d("StoreUpdateGatewayImpl.getLatestVersion",githubReleaseDTO.tag_name)
            return githubReleaseDTO.tag_name
        }catch (error: Throwable){
            // It's Useless to show error message for users
            Log.d("StoreUpdateGatewayImpl.getLatestVersion","Connection Time Out")
            return null
        }
    }
}