# Storage Access Framework
> 本文件主要规定各层面文件的落盘动线和缓存区使用规范

### 内部存储
>用于保存 Plugin 和 Environment；在非 ADB 模式下，相关文件会保存到此位置  
属于 `持久化` 设计，不会被 OS 的缓存清理机制不定期清除

典型路径有两条：

路径一：默认情况下，持久性存放 Plugin 的位置
```shell
~/files/Plugin
```
路径二：默认情况下，持久性存放 Environment 的位置
```shell
~/files/Environment
```

--- 
### 内部cache
>在提取分享链接时，用于将插件从 `内部存储` 临时转移到此处  
属于 `非持久化` 设计，会被 OS 的缓存清理机制不定期清除

典型路径有两条：

路径一：默认情况下，非持久化存放 Plugin 的位置
```shell
~/cache/Plugin
```
路径二：默认情况下，非持久化存放 Environment 的位置
```shell
~/cache/Environment
```

---
### 外部存储 (Sdcard侧)
>用于临时转移 `Shell` 插件和 `Magisk` 兼容插件；在 ADB 模式下，相关文件会转移到此位置  
属于 `非持久化` 设计

典型路径有三条：

路径一：默认情况下，非持久化存放 Plugin 的 `临时主要工作区`  
>主要负责提供用于解压 Magisk 插件包并暂存转译结果的 `根环境`  
>并用于 `暂存` 重新生成的 Rootless Store 风格插件包
```shell
/storage/emulated/0/Android/data/com.baidaidai.rootless_store/files/Magisk
```

路径二：默认情况下，非持久化存放 Plugin 的 `解压主要工作区`
>主要负责整理并暂存已解压、转译后的 Magisk 插件源文件
```shell
/storage/emulated/0/Android/data/com.baidaidai.rootless_store/files/Magisk/template
```

路径三：默认情况下，非持久化存放用于生成 RootlessStore Plugin 的 `临时文件位置`
>用于在后续流程中通过 stdio，将 `路径二` 中修改完成的文件  
>写入准备好的空 zip 中
```shell
/storage/emulated/0/Android/data/com.baidaidai.rootless_store/files/Magisk/_template_.zip
```

---
### 外部cache (Sdcard侧)
>在提取分享链接时，用于将插件从 `Shell存储` 临时转移到此处 
属于 `非持久化` 设计，会被 OS 的缓存清理机制不定期清除

典型路径只有一条：

路径一：默认情况下，非持久化存放 Plugin 分享包的位置
```shell
/storage/emulated/0/Android/data/com.baidaidai.rootless_store/cache
```
---
### Shell存储
