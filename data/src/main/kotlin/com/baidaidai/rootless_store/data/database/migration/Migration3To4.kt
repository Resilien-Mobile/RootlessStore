package com.baidaidai.rootless_store.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {

    override fun migrate(db: SupportSQLiteDatabase) {

        // 1. Create the new plugin status table
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `pluginStatus` (
                    `pluginID` TEXT NOT NULL,
                    `enabled` INTEGER NOT NULL,
                    `state` TEXT NOT NULL,
                    `origin` TEXT NOT NULL,
                    PRIMARY KEY(`pluginID`)
                )
            """.trimIndent()
        )

        // 2. Move the state columns from pluginInfo into pluginStatus (source -> origin)
        db.execSQL(
            """
                INSERT INTO `pluginStatus` (`pluginID`, `enabled`, `state`, `origin`)
                SELECT `pluginID`, `enabled`, `state`, `source`
                FROM `pluginInfo`
            """.trimIndent()
        )

        // 3. Rebuild pluginInfo without the state columns
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
                    `requiredEnvironment` TEXT NOT NULL,
                    `entryPoint` TEXT NOT NULL,
                    `pluginRunModel` TEXT NOT NULL,
                    `webUIEntryPoint` TEXT,
                    PRIMARY KEY(`pluginID`)
                )
            """.trimIndent()
        )

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
                    `requiredEnvironment`,
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
                    `requiredEnvironment`,
                    `entryPoint`,
                    `pluginRunModel`,
                    `webUIEntryPoint`
                FROM `pluginInfo`
            """.trimIndent()
        )

        // 4. Replace the old plugin info table
        db.execSQL("DROP TABLE `pluginInfo`")
        db.execSQL("ALTER TABLE `pluginInfo_new` RENAME TO `pluginInfo`")
    }
}
