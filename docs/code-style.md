# Naming

> 一个语义只使用一组固定词表达。命名应优先表达领域含义，而不是追求词汇丰富。

## Domain

项目中的核心领域词应保持稳定，不随意使用同义词替换。

```text
Plugin
PluginSource
PluginManifest
PluginExecution
PluginRuntime
RuntimeState
ExecutionContext
CodeBrick
Market
```

例如已经定义为 `Plugin` 的概念，不应在相同语义下交替使用 `Extension`、`Addon`、`Module`。

## Verbs

| Verb | Meaning |
|---|---|
| `get` | 获取一个当前值，不表达持续监听 |
| `find` | 查找，允许不存在 |
| `require` | 必须存在，否则失败 |
| `list` | 获取多个对象 |
| `observe` | 返回 `Flow`，持续观察变化 |
| `load` | 从本地持久层加载 |
| `fetch` | 从远端获取 |
| `refresh` | 从 authoritative source 更新本地已有状态 |
| `sync` | 两边状态同步 |
| `resolve` | 根据上下文或候选项推导最终结果 |
| `validate` | 判断输入是否合法 |
| `parse` | representation 转换为 structured model |
| `create` | 创建新的 domain entity |
| `add` | 加入已有集合或 registry |
| `register` | 注册到 registry 或 system |
| `install` | 使 Plugin 进入已安装状态 |
| `remove` | 从集合或配置中移除 |
| `delete` | 删除持久化对象 |
| `enable` | 转换为 enabled 状态 |
| `disable` | 转换为 disabled 状态 |
| `start` | 启动生命周期 |
| `stop` | 正常停止生命周期 |
| `abort` | 非正常或强制终止 |
| `execute` | 执行一个定义明确的 executable |
| `prepare` | 做好执行准备，但不执行 |
| `initialize` | 创建首次可用状态 |
| `restore` | 恢复之前保存的状态 |
| `recover` | 从异常、崩溃或不一致中恢复 |
| `reset` | 回到 baseline 或 default 状态 |
| `cleanup` | 移除残留或过期资源 |
| `reconcile` | 比较两个状态并消除差异 |
| `ensure` | 幂等地保证某个 postcondition 成立 |

## Grammar

### Acronym

Kotlin 标识符中的 acronym 应当像普通单词一样使用 camel case，而不是保留全大写形式。

避免：

```kotlin
PluginDAO
PluginDTO
pluginID
webUIURI
ADBShell
```

使用：

```kotlin
PluginDao
PluginDto
pluginId
webUiUri
AdbShell
```

该规则只约束 Kotlin 标识符，不要求同步修改数据库表名、数据库列名、序列化字段名或外部协议字段。

### Singular Operation

单数操作默认已经表示对单个对象进行操作，禁止使用 `One` 强调单数。

避免：

```kotlin

AddOnePluginSourceUseCase

DeleteOnePluginSourceUseCase

GetOnePluginUseCase

```

使用：

```kotlin

AddPluginSourceUseCase

DeletePluginSourceUseCase

GetPluginUseCase

```

只有当数量本身具有明确业务语义时，才应在名称中表达数量。

### UseCase

```text
[Verb][Domain][Qualifier?]UseCase
```

例如：

```kotlin
InstallPluginUseCase
ObservePluginSourcesUseCase
AddAuthenticatedPluginSourceUseCase
RecoverPluginRuntimeStateUseCase
```

### Repository

```text
[Domain]Repository
```

例如：

```kotlin
PluginRepository
PluginSourceRepository
```

### Dao

Dao 使用持久层语言，名称遵循：

```text
[Domain]Dao
```

例如：

```kotlin
PluginDao
PluginSourceDao
CodeBrickDao
```

Dao 所在层已经隐含本地持久化语义。读取方法应优先表达返回值的基数、可空性和是否持续观察，不应将所有读取方法统一命名为 `load`。

| Verb | Dao Meaning |
|---|---|
| `insert` | 插入持久化对象，冲突行为由 Room annotation 明确表达 |
| `upsert` | 插入或更新持久化对象，只在实际使用 `@Upsert` 时使用 |
| `update` | 更新已有持久化对象或字段 |
| `find` | 单次查找一个对象，允许不存在 |
| `require` | 单次查找一个对象，必须存在 |
| `get` | 单次获取确定存在的对象或 scalar value |
| `list` | 单次获取多个对象 |
| `observe` | 返回 `Flow`，持续观察持久化状态 |
| `delete` | 删除持久化对象 |

例如：

```kotlin
interface PluginDao {
    suspend fun insertPlugin(pluginEntity: PluginEntity)

    suspend fun updatePluginEnabled(
        pluginId: String,
        isEnabled: Boolean
    )

    suspend fun findPluginById(pluginId: String): PluginEntity?

    fun observePlugins(): Flow<List<PluginEntity>>

    fun observePluginCount(): Flow<Int>

    suspend fun getPluginCount(): Int

    suspend fun deletePluginById(pluginId: String)
}
```

Dao 中的通用字段更新可以使用 `update`。Repository 或 UseCase 再将其组织为 `enable`、`disable` 等领域状态转换。

```kotlin
// Dao
updatePluginEnabled(pluginId, isEnabled)

// Repository or UseCase
enablePlugin(pluginId)
disablePlugin(pluginId)
```

单数操作不使用 `One`。只有批量范围本身具有明确业务语义时，才应显式使用数量词，特别是具有破坏性的操作。

```kotlin
deletePlugin(pluginEntity)
deleteAllPluginExecutions()
disableAllPlugins()
```

### Boolean

统一使用：

```text
is / has / can / should / needs / supports
```

例如：

```kotlin
isInstalled
hasPermission
canExecute
needsRecovery
```

## Discouraged

避免使用语义过宽或没有实际意义的词：

```text
handle
process
manage
do
whole
one
data
info
helper
utils
```

优先寻找准确的 **Domain + Verb + Role**。
