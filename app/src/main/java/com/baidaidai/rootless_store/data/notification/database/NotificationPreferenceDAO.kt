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
    @Query(
        "UPDATE NotificationPreferenceEntity SET apiKey = :apiKey, notificationTitle = :notificationTitle, " +
            "selfBuiltServer = :selfBuiltServer, criticalWarning = :criticalWarning " +
            "WHERE _primaryKey_ = 'RootlessStoreNotificationPreferenceEntityPrimaryKey'"
    )
    suspend fun updateOneNotificationPreference(
        apiKey: String,
        notificationTitle: String? = null,
        selfBuiltServer: String? = null,
        criticalWarning: Boolean,
    )

    // Read
    @Query(
        "SELECT * FROM NotificationPreferenceEntity " +
            "WHERE _primaryKey_ = 'RootlessStoreNotificationPreferenceEntityPrimaryKey' LIMIT 1"
    )
    suspend fun getOneNotificationPreference(): NotificationPreferenceEntity?

    // Delete
}