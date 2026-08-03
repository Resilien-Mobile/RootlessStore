package com.baidaidai.rootless_store.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {

        // Create the new plugin info table
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `pluginInfo_new` (
                    `pluginID` TEXT NOT NULL,
                    `installedVersion` TEXT NOT NULL,
                    `pluginRenderingName` TEXT NOT NULL,
                    `pluginPackageName` TEXT NOT NULL,
                    `iconURI` TEXT,
                    `author` TEXT NOT NULL,
                    `pluginDescription` TEXT NOT NULL,
                    `enabled` INTEGER NOT NULL,
                    `requiredEnvironment` TEXT NOT NULL,
                    `state` TEXT NOT NULL,
                    `source` TEXT NOT NULL,
                    `entryPoint` TEXT NOT NULL,
                    `pluginRunModel` TEXT NOT NULL,
                    `webUIEntryPoint` TEXT,
                    PRIMARY KEY(`pluginID`)
                )
            """.trimIndent()
        )

        // Copy existing data without bypassEnvironment
        db.execSQL(
            """
                INSERT INTO `pluginInfo_new` (
                    `pluginID`,
                    `installedVersion`,
                    `pluginRenderingName`,
                    `pluginPackageName`,
                    `iconURI`,
                    `author`,
                    `pluginDescription`,
                    `enabled`,
                    `requiredEnvironment`,
                    `state`,
                    `source`,
                    `entryPoint`,
                    `pluginRunModel`,
                    `webUIEntryPoint`
                )
                SELECT
                    `pluginID`,
                    `installedVersion`,
                    `pluginRenderingName`,
                    `pluginPackageName`,
                    `iconURI`,
                    `author`,
                    `pluginDescription`,
                    `enabled`,
                    `requiredEnvironment`,
                    `state`,
                    `source`,
                    `entryPoint`,
                    `pluginRunModel`,
                    `webUIEntryPoint`
                FROM `pluginInfo`
            """.trimIndent()
        )

        // Replace the old plugin info table
        db.execSQL("DROP TABLE `pluginInfo`")
        db.execSQL("ALTER TABLE `pluginInfo_new` RENAME TO `pluginInfo`")
    }
}