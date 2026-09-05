package com.baidaidai.rootless_store.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.baidaidai.rootless_store.data.codebrick.database.CodeBrickDao
import com.baidaidai.rootless_store.data.codebrick.database.CodeBrickEntity
import com.baidaidai.rootless_store.data.database.converter.RoomCollectionTypeConverters
import com.baidaidai.rootless_store.data.execution.database.PluginExecutionDao
import com.baidaidai.rootless_store.data.execution.database.PluginExecutionEntity
import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceDao
import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceEntity
import com.baidaidai.rootless_store.data.environment.database.EnvironmentDao
import com.baidaidai.rootless_store.data.environment.database.EnvironmentEntity
import com.baidaidai.rootless_store.data.environment.database.EnvironmentStatusDao
import com.baidaidai.rootless_store.data.environment.database.EnvironmentStatusEntity
import com.baidaidai.rootless_store.data.plugin.database.PluginDao
import com.baidaidai.rootless_store.data.plugin.database.PluginEntity
import com.baidaidai.rootless_store.data.plugin.database.PluginStatusDao
import com.baidaidai.rootless_store.data.plugin.database.PluginStatusEntity
import com.baidaidai.rootless_store.data.source.database.PluginSourceDao
import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity

@Database(
    entities = [
        PluginEntity::class,
        PluginStatusEntity::class,
        PluginSourceEntity::class,
        PluginExecutionEntity::class,
        EnvironmentEntity::class,
        EnvironmentStatusEntity::class,
        NotificationPreferenceEntity::class,
        CodeBrickEntity::class
        // 其它表也一起加进来
    ],
    version = 5,
    exportSchema = true
)
@TypeConverters(RoomCollectionTypeConverters::class)
abstract class RootlessStoreDatabase : RoomDatabase() {
    abstract fun pluginDao(): PluginDao
    abstract fun pluginStatusDao(): PluginStatusDao
    abstract fun environmentDao(): EnvironmentDao
    abstract fun environmentStatusDao(): EnvironmentStatusDao
    abstract fun pluginSourceDao(): PluginSourceDao
    abstract fun pluginExecutionDao(): PluginExecutionDao
    abstract fun notificationPreferenceDao(): NotificationPreferenceDao
    abstract fun codeBrickDao(): CodeBrickDao

    // 其它 DAO 也在这里暴露
}
