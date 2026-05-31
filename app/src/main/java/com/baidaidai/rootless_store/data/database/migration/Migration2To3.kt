package com.baidaidai.rootless_store.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `NotificationPreferenceEntity_new` (
                    `_primaryKey_` TEXT NOT NULL,
                    `apiKey` TEXT NOT NULL,
                    `notificationTitle` TEXT,
                    `selfBuiltServer` TEXT,
                    `criticalWarning` INTEGER NOT NULL,
                    PRIMARY KEY(`_primaryKey_`)
                )
            """.trimIndent()
        )

        db.execSQL(
            """
                INSERT INTO `NotificationPreferenceEntity_new` (
                    `_primaryKey_`,
                    `apiKey`,
                    `notificationTitle`,
                    `selfBuiltServer`,
                    `criticalWarning`
                )
                SELECT
                    'RootlessStoreNotificationPreferenceEntityPrimaryKey',
                    `apiKey`,
                    NULL,
                    NULL,
                    `criticalWarning`
                FROM `NotificationPreferenceEntity`
                LIMIT 1
            """.trimIndent()
        )

        db.execSQL("DROP TABLE `NotificationPreferenceEntity`")
        db.execSQL(
            """
                ALTER TABLE `NotificationPreferenceEntity_new`
                RENAME TO `NotificationPreferenceEntity`
            """.trimIndent()
        )
    }
}