package com.baidaidai.rootless_store.domain.tile.model

import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.baidaidai.rootless_store.application.codebrick.ExecuteCodeBrickUseCase
import com.baidaidai.rootless_store.application.codebrick.FindCodeBrickByTileIndexUseCase
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class RootlessStoreTileService: TileService() {

    abstract val tileIndex: Int

    @Inject
    lateinit var findCodeBrickByTileIndexUseCase: FindCodeBrickByTileIndexUseCase

    @Inject
    lateinit var executeCodeBrickUseCase: ExecuteCodeBrickUseCase


    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var _codeBrickConfig: CodeBrickConfig? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartListening() {
        super.onStartListening()

        serviceScope.launch {
            _codeBrickConfig = findCodeBrickByTileIndexUseCase(tileIndex)
            refreshTileContent()
        }
    }

    override fun onClick() {
        super.onClick()

        serviceScope.launch {
            val codeBrickConfig = _codeBrickConfig
            if (codeBrickConfig != null){
                val resultFlow = executeCodeBrickUseCase(codeBrickConfig)
                resultFlow.collect {
                    // Do Noting
                    // TODO("Can Collect, Dispatch to notification")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun refreshTileContent(){
        val codeBrickConfig = _codeBrickConfig

        if (codeBrickConfig != null){
            qsTile?.apply {
                label = codeBrickConfig.codeBrickTitle
                subtitle = codeBrickConfig.codeBrickContent
                contentDescription = codeBrickConfig.codeBrickTitle
                updateTile()
            }
        }
    }

}
