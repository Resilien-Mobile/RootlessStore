package com.baidaidai.rootless_store.data.market.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.data.market.mapper.MarketMapper.toMarketPageResponse
import com.baidaidai.rootless_store.data.market.remote.api.MarketApi
import com.baidaidai.rootless_store.data.market.remote.dto.MarketPageResponseDto
import com.baidaidai.rootless_store.domain.market.error.MarketError
import com.baidaidai.rootless_store.domain.market.model.MarketManifest
import io.ktor.client.call.body

class MarketManifestPagingSource (
    private val marketApi: MarketApi,
    private val pluginSourceEndpoint: String,
    private val onError: suspend (MarketError)-> Unit
) : PagingSource<Int, MarketManifest>() {

    // Core Suspend Method for Paging fetching Data
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MarketManifest> {
        try{

            // Initial the page key curser
            val page = params.key ?: 0

            // HTTP Request
            val response = marketApi.fetchMarketManifests(
                pageNumber = page,
                pluginSourceEndpoint = pluginSourceEndpoint
            )
            val marketPageResponseDto = response.body<MarketPageResponseDto>()
            val marketPageResponse = marketPageResponseDto.toMarketPageResponse()

            val nextKey = if (marketPageResponseDto.meta.hasMore) page + 1 else null

            return LoadResult.Page(
                data = marketPageResponse.manifests,
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )

        }catch (error: Throwable){
            onError(
                MarketError(
                    errorMessage = error.message ?: "Fetch market manifests failed",
                    errorCause = error.stackTrace.formatAsMultilineString()
                )
            )
            return LoadResult.Error(error)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MarketManifest>): Int? {
        TODO("Not yet implemented")
    }
}
