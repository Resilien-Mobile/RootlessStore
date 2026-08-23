package com.baidaidai.rootless_store.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.baidaidai.rootless_store.data.codebrick.database.CodeBrickDao
import com.baidaidai.rootless_store.data.codebrick.database.CodeBrickEntity
import com.baidaidai.rootless_store.data.database.repository.RoomConvertRepositoryImpl
import com.baidaidai.rootless_store.data.execution.database.PluginExecutionDao
import com.baidaidai.rootless_store.data.execution.database.PluginExecutionEntity
import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceDao
import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceEntity
import com.baidaidai.rootless_store.data.environment.database.EnvironmentDao
import com.baidaidai.rootless_store.data.environment.database.EnvironmentInfoEntity
import com.baidaidai.rootless_store.data.plugin.database.PluginDao
import com.baidaidai.rootless_store.data.plugin.database.PluginInfoEntity
import com.baidaidai.rootless_store.data.source.database.PluginSourceDao
import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity

@Database(
    entities = [
        PluginInfoEntity::class,
        PluginSourceEntity::class,
        PluginExecutionEntity::class,
        EnvironmentInfoEntity::class,
        NotificationPreferenceEntity::class,
        CodeBrickEntity::class
        // 其它表也一起加进来
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(RoomConvertRepositoryImpl::class)
abstract class RootlessStoreDatabase : RoomDatabase() {
    abstract fun pluginDao(): PluginDao
    abstract fun environmentDao(): EnvironmentDao
    abstract fun pluginSourceDao(): PluginSourceDao
    abstract fun pluginExecutionDao(): PluginExecutionDao
    abstract fun notificationPreferenceDao(): NotificationPreferenceDao
    abstract fun codeBrickDao(): CodeBrickDao

    // 其它 DAO 也在这里暴露
}
