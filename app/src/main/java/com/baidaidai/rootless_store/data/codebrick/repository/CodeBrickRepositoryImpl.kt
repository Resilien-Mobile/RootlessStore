package com.baidaidai.rootless_store.data.codebrick.repository

import com.baidaidai.rootless_store.data.codebrick.mapper.CodeBrickMapper.toCodeBrickConfig
import com.baidaidai.rootless_store.data.codebrick.mapper.CodeBrickMapper.toCodeBrickEntity
import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CodeBrickRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase
) {
    private val codeBrickDAO = rootlessStoreDatabase.codeBrickDao()

    // Create
    suspend fun createOneCodeBrickConfig(
        codeBrickConfig: CodeBrickConfig
    ) {
        val codeBrickEntity = codeBrickConfig.toCodeBrickEntity()
        codeBrickDAO.createOneCodeBrickConfig(codeBrickEntity)
    }

    // Update
    suspend fun updateOneCodeBrickConfig(
        codeBrickConfig: CodeBrickConfig
    ) {
        val codeBrickEntity = codeBrickConfig.toCodeBrickEntity()
        codeBrickDAO.updateOneCodeBrickConfig(codeBrickEntity)
    }

    // Read
    suspend fun getOneCodeBrickConfig(
        unixTimeStamp: Long
    ): CodeBrickConfig? {
        return codeBrickDAO
            .getOneCodeBrickConfig(unixTimeStamp)
            ?.toCodeBrickConfig()
    }

    fun getAllCodeBrickConfig(): Flow<List<CodeBrickConfig>> {
        return codeBrickDAO
            .getAllCodeBrickConfig()
            .map { codeBrickEntityList ->
                codeBrickEntityList.map { codeBrickEntity ->
                    codeBrickEntity.toCodeBrickConfig()
                }
            }
    }

    // Delete
    suspend fun deleteOneCodeBrickConfig(
        codeBrickConfig: CodeBrickConfig
    ) {
        val codeBrickEntity = codeBrickConfig.toCodeBrickEntity()
        codeBrickDAO.deleteOneCodeBrickConfig(codeBrickEntity)
    }
}
