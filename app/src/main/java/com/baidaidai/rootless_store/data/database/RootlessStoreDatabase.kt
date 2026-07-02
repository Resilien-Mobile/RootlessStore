package com.baidaidai.rootless_store.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.baidaidai.rootless_store.data.codebrick.database.CodeBrickDAO
import com.baidaidai.rootless_store.data.codebrick.database.CodeBrickEntity
import com.baidaidai.rootless_store.data.database.repository.RoomConvertRepositoryImpl
import com.baidaidai.rootless_store.data.execute.database.PluginExecuteStatusDAO
import com.baidaidai.rootless_store.data.execute.database.PluginExecuteStatusEntry
import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceDAO
import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceEntity
import com.baidaidai.rootless_store.data.environment.database.EnvironmentInfoDAO
import com.baidaidai.rootless_store.data.environment.database.EnvironmentInfoEntity
import com.baidaidai.rootless_store.data.plugin.database.PluginInfoDAO
import com.baidaidai.rootless_store.data.plugin.database.PluginInfoEntity
import com.baidaidai.rootless_store.data.source.database.PluginSourceDAO
import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity

@Database(
    entities = [
        PluginInfoEntity::class,
        PluginSourceEntity::class,
        PluginExecuteStatusEntry::class,
        EnvironmentInfoEntity::class,
        NotificationPreferenceEntity::class,
        CodeBrickEntity::class
        // 其它表也一起加进来
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConvertRepositoryImpl::class)
abstract class RootlessStoreDatabase : RoomDatabase() {
    abstract fun pluginInfoDao(): PluginInfoDAO
    abstract fun environmentInfoDao(): EnvironmentInfoDAO
    abstract fun pluginSourceDao(): PluginSourceDAO
    abstract fun pluginExecuteStatusDao(): PluginExecuteStatusDAO
    abstract fun notificationPreferenceDao(): NotificationPreferenceDAO
    abstract fun codeBrickDao(): CodeBrickDAO

    // 其它 DAO 也在这里暴露
}
