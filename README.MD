# NeteaseMuMu

用于在非 MuMu 环境下尝试启用 MuMu 渠道提供的 iOS、Android 平台选择及扫码相关功能。

## 功能与原理

| 功能 | 当前实现 |
| --- | --- |
| MuMu 设备标识 | [SystemHook](app/src/main/java/me/naptila/neteasemumu/hook/hooks/SystemHook.kt) 拦截 `SystemProperties.get(String, String)` 对 `ro.build.id` 的读取，返回 `V417IR`。 |
| MuMu 渠道 | [ChannelHook](app/src/main/java/me/naptila/neteasemumu/hook/hooks/ChannelHook.kt) 在反射查找阶段返回模块内的代理方法，将渠道设为 `netease.mumu2_free_dev`。 |

渠道代理匹配以下方法的反射查找：

- `com.netease.ntunisdk.base.utils.cps.ApkChanneling.getChannel(File)`。
- `com.netease.ntunisdk.Sdk*` 类的 `getCustomCpsChannel(Object[])`。

## 使用方法

运行环境：

- Android 9（API 28）及以上。
- 可加载本模块的 Xposed 兼容框架；模块声明的最低 Xposed API 为 `93`。
- 模块没有独立设置界面，需要通过框架加载到目标游戏进程。

### LSPatch 内嵌

1. 从本仓库的 Releases 页面下载模块 APK。
2. 在 LSPatch 中选择需要使用的目标游戏 APK，并将本模块 APK 添加为内嵌模块。
3. 完成补丁制作，安装生成的目标应用 APK，再启动游戏。
4. 模块更新后，重新使用新模块 APK 给目标应用打补丁。

GitHub Release 中提供的是模块 APK。内嵌模式下，单独更新模块 APK 不会替换已经嵌入目标应用的旧版本。

### Xposed / LSPosed 兼容框架

1. 安装模块 APK，并在框架管理器中启用模块。
2. 手动选择需要生效的目标游戏作为作用域。
3. 重启目标游戏，使模块在新进程中加载。

## 从源码构建

| 配置 | 版本 |
| --- | --- |
| JDK / Gradle Daemon | 25 |
| Gradle Wrapper | 9.6.0 |
| Android Gradle Plugin | 9.5.1 |
| compileSdk / targetSdk | 37 |
| CI Build Tools | 36.0.0 |
| minSdk | 28 |

使用项目自带的 Gradle Wrapper 构建 Debug APK。

Windows PowerShell：

```powershell
.\gradlew.bat :app:assembleDebug
```

Linux / WSL：

```bash
bash ./gradlew :app:assembleDebug
```

产物位于 `app/build/outputs/apk/debug/app-debug.apk`。本地 Debug 构建不需要配置发布签名。
