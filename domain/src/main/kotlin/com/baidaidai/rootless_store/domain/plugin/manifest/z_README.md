# 各类 Manifest 分类

## RootlessManifestCollection

`RootlessManifestCollection` 作为联合 sealed 类型，掌管着 `PluginManifest` 和 `EnvironmentManifest` 两大基础类型。

它本身不描述某一种具体可安装对象，而是作为统一入口，让市场、安装、列表渲染等场景可以用同一个父类型承载不同 Manifest。

```text
RootlessManifestCollection
├── PluginManifest
└── EnvironmentManifest
```

## PluginManifest

`PluginManifest` 作为插件的主领，除了自己，还掌管着 `Local`、`Remote`、`Room` 三种类型。

插件 Manifest 描述的是可执行插件本体。它包含插件名称、包名、版本、作者、描述、运行入口、运行模型、所需宿主能力等核心信息。

```text
PluginManifest
├── PluginManifestLocal
├── PluginManifestRemote
└── PluginManifestRoom
```

### PluginManifestLocal

`PluginManifestLocal` 表示从本地插件包中读取出来的插件 Manifest。

它主要用于本地安装流程，例如用户选择一个 zip 插件包后，应用从包内读取 `PluginManifest.json`，再解析成 `PluginManifestLocal`。

典型职责：

- 描述本地插件包内声明的静态信息
- 作为本地安装流程的输入
- 可以转换为 `PluginManifestRoom` 后写入数据库

### PluginManifestRemote

`PluginManifestRemote` 表示从远程市场接口获取到的插件 Manifest。

它除了包含插件基础信息，还需要包含远程下载地址，例如 `pluginURI`。应用在 Market 页面展示远程插件时，通常使用这个类型。

典型职责：

- 描述市场接口返回的插件信息
- 提供远程插件包下载地址
- 作为从 Market 安装插件的输入
- 可以转换为 `PluginManifestRoom` 后写入数据库

### PluginManifestRoom

`PluginManifestRoom` 表示已经进入本地数据库管理范围的插件 Manifest。

它不仅包含插件基础信息，还包含本地运行状态，例如是否启用、插件状态、来源等。

典型职责：

- 表示已安装或已记录到数据库的插件
- 参与本地插件列表渲染
- 参与插件启用、禁用、执行、卸载等本地管理流程

## EnvironmentManifest

`EnvironmentManifest` 作为运行环境的主领，除了自己，还掌管着 `Local`、`Remote`、`Room` 三种类型。

环境 Manifest 描述的是插件运行所需的环境包。它和插件 Manifest 类似，但重点不在插件本体，而在环境路径、动态库路径、环境变量等运行时能力。

```text
EnvironmentManifest
├── EnvironmentManifestLocal
├── EnvironmentManifestRemote
└── EnvironmentManifestRoom
```

### EnvironmentManifestLocal

`EnvironmentManifestLocal` 表示从本地环境包中读取出来的环境 Manifest。

它主要用于本地安装环境包时，从包内读取 `EnvironmentManifest.json` 并解析得到环境声明。

典型职责：

- 描述本地环境包内声明的静态信息
- 作为本地安装环境包流程的输入
- 可以转换为 `EnvironmentManifestRoom` 后写入数据库

### EnvironmentManifestRemote

`EnvironmentManifestRemote` 表示从远程市场接口获取到的环境 Manifest。

它除了包含环境基础信息，还需要包含远程下载地址，例如 `environmentURI`。

典型职责：

- 描述市场接口返回的环境包信息
- 提供远程环境包下载地址
- 作为从 Market 安装环境包的输入
- 可以转换为 `EnvironmentManifestRoom` 后写入数据库

### EnvironmentManifestRoom

`EnvironmentManifestRoom` 表示已经进入本地数据库管理范围的环境 Manifest。

它不仅包含环境基础信息，还包含本地启用状态、环境状态、来源等运行时管理信息。

典型职责：

- 表示已安装或已记录到数据库的环境包
- 参与本地环境列表渲染
- 为插件执行提供 `PATH`、`LD_LIBRARY_PATH`、环境变量等运行时配置

## 类型关系总览

```text
RootlessManifestCollection
├── PluginManifest
│   ├── PluginManifestLocal
│   ├── PluginManifestRemote
│   └── PluginManifestRoom
└── EnvironmentManifest
    ├── EnvironmentManifestLocal
    ├── EnvironmentManifestRemote
    └── EnvironmentManifestRoom
```

## Local / Remote / Room 的区别

| 类型 | 来源 | 主要用途 |
| --- | --- | --- |
| `Local` | 本地 zip 包内的 Manifest 文件 | 本地安装前的解析结果 |
| `Remote` | 远程 Market 接口 | 市场展示和远程下载安装到本地 |
| `Room` | 本地 Room 数据库 | 已安装对象的本地管理状态 |

简单来说：

- `Local` 面向本地文件安装。
- `Remote` 面向远程市场展示和下载。
- `Room` 面向安装后的本地持久化管理。
