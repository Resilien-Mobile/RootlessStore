package com.baidaidai.rootless_store.service.tile

import com.baidaidai.rootless_store.domain.tile.model.RootlessStoreTileService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CustomTileService_4 @Inject constructor(
    override val tileIndex: Int = 4
): RootlessStoreTileService()