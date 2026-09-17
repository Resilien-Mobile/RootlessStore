<img src="/asset/banner/RootlessStore.png"></img>

<h1 style="width: 100%; display: flex; justify-content: center; align-items: center;">Rootless Store</h1>

<div style="width: 100%; display: flex; flex-direction: row ; justify-content: center; align-items: center;">
    <a href="/README.md">English Version</a>
    <div style="margin-left: 10px; margin-right: 10px ">·</div>
    <a href="https://resilien-mobile.github.io/RootlessStore_WiKi/">官方 Wiki</a>
</div>

<h4 style="width: 100%; display: flex; justify-content: center; align-items: center; margin-bottom: 30px">一个开源的、面向 Android 生态的 rootless 插件管理与运行平台</h4>

<p>
    <img src="/asset/picture/HomeScreen.png" width="32%" />
    <img src="/asset/picture/PluginScreen.png" width="32%" />
    <img src="/asset/picture/CodeBrickScreen.png" width="32%" />
    <img src="/asset/picture/SettingScreen.png" width="32%" />
    <img src="/asset/picture/ExecuteScreen.png" width="32%" />
    <img src="/asset/picture/ShellScreen.png" width="32%" />
</p>

## 项目简介

Rootless Store 想做的，不只是一个“安装插件”的工具。
它更想为 Android 生态补上一块长期缺失的拼图：

- 让插件可以被发现、管理和执行
- 让 Source 可以被组织、追踪和维护
- 让更多用户用更低门槛的方式接触 Android 与类 Linux 能力

它坚持三件事：

- **免 Root / 低门槛**
- **开源 / 可维护**
- **去中心化 / 可扩展**

## 功能特性

- 从可配置的 Source 中管理、执行、分享和安装插件
- 使用 CodeBrick 快速构建自动化片段，并在需要时升级成完整插件
- 同时支持一次性脚本和面向长时间工作流的 Daemon 插件
- 按需选择执行上下文：受限 App Shell、Shizuku / ADB 或 Root
- 接收插件状态变化和 warning 事件的远程通知
- 监控设备状态，包括 Memory、Storage、Kernel、SELinux、Plugin 状态和温度
- 使用 GUI 优先的工作流，降低传统 TUI / TTY 工具的使用门槛

## 未来规划

- [x] 完成核心页面与文档结构
- [x] 建立 Source 与 Market 的基础接口
- [x] 建立运行时插件开发文档
- [x] 改进 Market 与插件详情 UI
- [x] 支持插件终止通知
- [x] 第三方通知推送
- [x] 更多个性化插件
- [x] Daemon 完整支持
- [x] 插件状态切换清理
- [x] 支持偏好设置面板
- [x] 支持私有源、不可见源与付费源
- [x] Shell 代码片段支持
- [ ] Base64 CodeBrick Token 支持
- [x] Magisk 插件兼容层
- [x] Android 控制中心快捷启动磁贴
- [x] 更具表现力的 Host 状态面板
- [x] 添加测试矩阵（尚未完全完成）
- [x] 更客观的错误原因
- [ ] 证书签名防篡改验证链
- [ ] 更直观地展示插件执行方式
- [ ] 完善过滤、状态反馈与权限边界
- [ ] 发布到 F-Droid

## 为什么我做了 Rootless Store？

因为我始终觉得，Android 生态并不缺能力。
它缺的是一个真正像样的入口，把大家凝聚起来。

Shizuku、Magisk、KernelSU、ADB、Shell、Root……  
这些东西都很强，但它们长期停留在碎片化信息、零散脚本和高门槛命令行里。  
有能力的人很多，真正容易接近的生态却很少。

所以 Rootless Store 想做的，就是把这些能力重新组织起来。  
不是把技术变成黑箱，  
而是把理解权和使用权尽可能还给更多人。

## 我的立场

我一直相信一句话：

> **真相终将戳破谎言，科技应人人平等。**

从 Animora 到 Rootless Store，  
我真正想做的都不是“堆功能”，而是：

- 让代码更清楚
- 让文档更友好
- 让能力不只属于少数人
- 让开源真正成为一种可参与的现实

## Rootless Store 想传达什么？

- Android 生态值得拥有自己的插件基础设施
- 技术不应该永远只掌握在少数人手里
- 开源不只是公开代码，更是公开理解权
- 能力不该成为壁垒，而应该成为桥梁

## 最后

Rootless Store 想做的事情很简单：

**为 Android 生态补齐最后一枚拼图。**

不是为了制造新的门槛，  
而是为了把原本就存在的能力，  
用更清晰、更开放、更友好的方式重新组织起来。

---

### 为了那些
**依然相信开放、依然愿意探索、依然选择希望的人**
