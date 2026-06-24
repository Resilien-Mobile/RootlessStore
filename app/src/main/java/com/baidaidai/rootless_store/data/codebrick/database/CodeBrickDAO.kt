package com.baidaidai.rootless_store.data.codebrick.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeBrickDAO {
    /**
     * CURD
     */

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOneCodeBrickConfig(codeBrickEntity: CodeBrickEntity)

    // Update
    @Update
    suspend fun updateOneCodeBrickConfig(codeBrickEntity: CodeBrickEntity)

    // Read
    @Query("SELECT * FROM CodeBrickEntity WHERE unixTimeStamp = :unixTimeStamp LIMIT 1")
    suspend fun getOneCodeBrickConfig(unixTimeStamp: Long): CodeBrickEntity?

    @Query("SELECT * FROM CodeBrickEntity")
    fun getAllCodeBrickConfig(): Flow<List<CodeBrickEntity>>

    // Delete
    @Delete
    suspend fun deleteOneCodeBrickConfig(codeBrickEntity: CodeBrickEntity)
}
