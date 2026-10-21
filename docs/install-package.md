<!--
version: v0.1.0,
author: CreaterBai
-->
# install Package
> 本文件主要讲解 Rootless Store 对于安装 Plugin 过程的细节处理

### 1. 包签名检测（2.x兼容）

由于 Environment 和 Plugin 在签名结构的包装下，行为特征类似，故我们需要在处理链的下一跳中进行初筛选  
Rootless Store 的 2.x 和 3.x 差别在于包签名检测失败后果不一样：

- 2.x 检测到未签名裸 zip 包结构，会直接失败回退，功能仅做为向 3.x 的开发者过度阶段
- 3.x 检测到未签名裸 zip 包结构，会弹出警告，需用户手动确认后方可安装，并且不记录开发者信息

至于何为 功能过度 和如何 探测包结构 的，大致会进行如下步骤

1. 根据固定结构进行状态建模：abc.zip -> ( package.zip, sign.p7s )
2. 根据 sign.p7s 里 X.509 证书链，进行公钥对 package.zip 校验
   1. 若公钥签名比对成功，且被 Rootless Store 所签名的证书，则显示 “对勾” 字样
   2. 若公钥签名比对成功，但未被 Rootless Store 所签名的证书，则显示 “存疑” 字样。  
   需用户手动确认后方可安装，会记录开发者证书信息，后续安装同一开发者签名的 package，则显示 “对勾” 字样
   3. 若不存在签名，为裸 zip 包结构，会弹出警告，需用户手动确认后方可安装，并且不记录开发者信息

### 2. 包类型检测

当 Rootless Store 的 [InstallLocalPackageUseCase](../application/src/main/kotlin/com/baidaidai/rootless_store/application/install/InstallLocalPackageUseCase.kt) 收到来自用户从 SAF 拿到的指向特定待确认 Package 包时，
UseCase 会先进行 ShadowSniff 的动作，即委派 [AndroidFileSystemReadOperatorGatewayImpl](../data/src/main/kotlin/com/baidaidai/rootless_store/data/fileSystem/gateway/AndroidFileSystemReadOperatorGatewayImpl.kt)
进行浅层初筛，用于区分 Package 包的所属类型

ShadowSniff 不会直接解压 Package，也不会执行其中的任何脚本，仅通过读取压缩包内的特征文件判断其类型。当前可识别的特征文件有三类：

1. `PluginManifest.json`：Rootless Store 原生 Plugin 包
2. `EnvironmentManifest.json`：Rootless Store 原生 Environment 包
3. `module.prop`：兼容 Magisk 结构的 Plugin 包

探测顺序保持固定：Plugin、Environment、MagiskModule。若同一个包内同时出现多个特征文件，则以优先探测到的类型为准；若无法读取任何有效特征文件，则认为该 Package 不属于 Rootless Store 支持的安装类型。

ShadowSniff 只负责回答“这是什么包”，不负责回答“这个包是否可信”。可信度判断由上一节中的签名检测负责，两个动作在职责上彼此分离。

### 3. 包信息解析与安装确认

完成包类型初筛后，Rootless Store 会委派 [ResolveLocalPackageMetaInfoUseCase](../application/src/main/kotlin/com/baidaidai/rootless_store/application/install/ResolvePackageSignInfoUseCase.kt) 读取对应的 Manifest 或 `module.prop`，建立待安装包的基础信息：

- Package 显示名称
- 开发者或作者名称
- Package 类型
- 签名状态
- 开发者证书状态

如果包结构和描述信息可以被正确解析，则进入安装确认界面。确认界面不会执行 Package 内的入口文件，只用于向用户展示待安装对象及其风险状态。

在 2.x 中，确认界面主要承担两类工作：

1. 对已通过 Rootless Store 信任链校验的 Package，显示可直接安装状态
2. 对未签名或签名存疑的 Package，显示风险提示，并要求用户进行明确确认

若 Package 存在签名错误、Manifest 无法解析，或 Package 类型无法确定，则不会进入正常安装流程。

### 4. 安装分流

用户确认后，`InstallLocalPackageUseCase` 会再次依据 ShadowSniff 的结果，将 Package 分发给对应的安装 UseCase。安装分流只负责选择正确的安装路径，不会改变 Package 的原始内容。

#### 4.1 Plugin 安装

Rootless Store 首先读取并解析 `PluginManifest.json`，确认 Plugin 的基本信息、入口文件和所需执行环境。

对于普通执行环境的 Plugin，Rootless Store 会交由 [InstallPluginUseCase](../application/src/main/kotlin/com/baidaidai/rootless_store/application/plugin/InstallPluginUseCase.kt) 处理，安装过程大致为：

1. 将 Package 解压到内部持久性目录
2. 根据 `pluginPackageName` 建立独立的 Plugin 目录
3. 设置入口文件的可执行属性
4. 将 Plugin Manifest 写入本地管理数据库
5. 创建对应的 Plugin 状态记录

对于要求 ADB 环境的 Plugin，则交由 Shizuku Shell 安装流程处理。Package 会先被转移到临时工作区，再由 Shell 侧完成安装。临时文件在流程结束后删除，Rootless Store 只保留用于展示和管理的 Manifest 信息。

如果 ADB Plugin 同时提供 WebUI，则还需要保留一份 Rootless Store 内部副本，用于在应用侧加载 WebUI 内容。

#### 4.2 Environment 安装

Environment 与 Plugin 使用相似的 Manifest 读取方式，但最终落盘位置和运行配置不同，具体由 [InstallEnvironmentUseCase](../application/src/main/kotlin/com/baidaidai/rootless_store/application/environment/InstallEnvironmentUseCase.kt) 负责。

安装过程大致为：

1. 读取并解析 `EnvironmentManifest.json`
2. 将 Package 解压到内部 Environment 目录
3. 根据 `environmentPackageName` 建立独立目录
4. 设置 Environment 入口文件的可执行属性
5. 保存 Environment Manifest
6. 创建对应的 Environment 状态记录

Environment 的运行路径、动态库路径和环境变量均由 Manifest 提供，安装过程只负责准备文件和注册信息，不在安装阶段启动 Environment。

#### 4.3 Magisk 兼容安装

对于包含 `module.prop` 但不包含 Rootless Store Manifest 的 Package，Rootless Store 会交由 [InstallMagiskPluginUseCase](../application/src/main/kotlin/com/baidaidai/rootless_store/application/plugin/InstallMagiskPluginUseCase.kt) 处理，并先通过 IllusionCube 校验、解析其属性内容。

解析成功后，将 Magisk 模块的信息转换为 Rootless Store 的 Plugin Manifest，并根据包内是否存在 `action.sh` 选择对应入口；不存在时使用 `service.sh` 作为默认入口。

之后，Rootless Store 会在临时工作区中完成以下转换：

1. 解压原始 Magisk Package
2. 写入生成的 `PluginManifest.json`
3. 重新打包为 Rootless Store 可识别的临时 Plugin 包
4. 通过 Shizuku Shell 完成安装
5. 清理转换过程产生的临时目录和临时压缩包

该过程不会修改用户选择的原始 Package。

### 5. 文件落盘与状态注册

安装过程中的文件区域分为持久性目录和临时目录两类。

普通 Plugin 和 Environment 使用应用内部存储作为持久性区域，分别对应：

```shell
~/files/Plugin/<pluginPackageName>
~/files/Environment/<environmentPackageName>
```

ADB Plugin 和 Magisk 兼容 Plugin 使用外部应用目录作为临时工作区。临时工作区只用于文件转移、格式转换和 Shell 交接，不作为 Rootless Store 的长期管理目录。

文件准备成功后，才会写入数据库和状态记录。数据库中的 Manifest 负责描述 Package，状态记录负责描述 Package 当前是否启用以及其来源。

因此，安装流程的顺序固定为：

```text
读取 -> 校验 -> 确认 -> 解压/转换 -> 设置入口 -> 注册状态
```

其中任意一步失败，都不应进入下一步的状态注册阶段。

### 6. 失败回退

安装流程中的失败主要分为四类：

1. Package 无法读取，或不是有效的 ZIP 结构
2. Package 类型无法确定
3. Manifest、`module.prop` 或签名信息无法解析
4. 文件解压、入口设置、Shell 安装或状态注册失败

发生失败时，UseCase 会返回结构化的 `PluginError`，由 UI 层统一展示错误信息。错误信息分为错误摘要、具体原因和辅助说明，便于区分用户输入问题、Package 本身问题和运行环境问题。

对于已经创建但尚未完成注册的临时文件，应在失败路径中进行清理；对于已经进入持久性目录但未完成注册的内容，不应被当作已安装 Package 展示。

### 7. 安装流程边界

Rootless Store 的本地安装流程只负责以下范围：

- 识别 Package 类型
- 校验 Package 的签名和基本结构
- 展示安装前信息
- 将 Package 安装到对应的运行区域
- 注册本地管理信息和状态

以下行为不属于本流程：

- 执行 Plugin 或 Environment 的业务入口
- 判断入口脚本的具体业务逻辑
- 替代 Shizuku 或 Shell 环境提供运行权限
- 修改用户原始 Package

安装完成只代表 Package 已经进入 Rootless Store 的管理范围，不代表其中的 Plugin 或 Environment 已经被启用或运行。

