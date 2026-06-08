# Explain Room
Room 是基于 SQLite 的 Jetpack 持久化层，主要提供：
- 类型安全的查询与编译期校验
- 清晰的 Entity / DAO / Database 分层
- 统一的本地数据库入口

本模块中，Room 用于在本地保存 RootlessStore 的插件元数据。

## Entity
`PluginInfoEntity` 定义了 `pluginInfo` 表，并使用 `pluginID` 作为主键。
字段与 `PluginManiFest` 一一对应，Room 可以直接把查询结果映射为该数据类。

如果后续加入更复杂的字段类型，需要提供 `@TypeConverter` 来保证可持久化。

---

## DAO
`PluginInfoDAO` 声明了数据库操作：
- `insertOneEntirePluginInfo` / `deleteOneEntirePluginInfo`
- 按 `pluginID` 查询单条插件信息
- 查询全部插件信息

查询方法为 `suspend`，用于协程调用。

---

## DataBase
`AppDatabase` 是 Room 数据库定义：
- 包含 `PluginInfoEntity`
- 版本号为 1，`exportSchema = true`
- 通过 `pluginInfoDao()` 暴露 DAO

通过 `Room.databaseBuilder(...)` 创建数据库实例。`PluginInfoDataBase.kt`
里注释掉的 Hilt 模块提供了一个常见配置示例。
