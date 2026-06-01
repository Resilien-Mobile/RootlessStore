package com.baidaidai.rootless_store.data.notification.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotificationPreferenceDAO {
    /**
     * CURD
     */

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOneNotificationPreference(
        notificationPreferenceEntity: NotificationPreferenceEntity
    )

    // Update

    // Read
    @Query("SELECT * FROM NotificationPreferenceEntity LIMIT 1")
    suspend fun getOneNotificationPreference(): NotificationPreferenceEntity?

    // Delete
}