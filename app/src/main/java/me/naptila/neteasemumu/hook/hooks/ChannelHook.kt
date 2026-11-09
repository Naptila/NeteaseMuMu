package me.naptila.neteasemumu.hook.hooks

import de.robv.android.xposed.XC_MethodHook
import me.naptila.neteasemumu.hook.Hook
import me.naptila.neteasemumu.hook.builder.hook
import me.naptila.neteasemumu.hook.builder.method
import java.io.File

/**
 * 如果是MuMu模拟器下载的包不需要此Hook，MuMu模拟器下载的包里面以及包含了渠道的签名块了。
 */
object ChannelHook : Hook() {
    private const val CHANNEL = "netease.mumu2_free_dev"
    private const val APK_CHANNELING_CLASS = "com.netease.ntunisdk.base.utils.cps.ApkChanneling"
    private const val SDK_CLASS_PREFIX = "com.netease.ntunisdk.Sdk"

    private var installed = false

    @Synchronized
    override fun hook(classLoader: ClassLoader) {
        if (installed) return

        val registrations = ArrayList<XC_MethodHook.Unhook>()

        try {
            val objectArrayType = arrayOfNulls<Any>(0).javaClass
            val classArrayType = arrayOf<Class<*>>().javaClass
            val apkProxy = ChannelHook::class.java.method("getChannel", File::class.java)
                .apply { isAccessible = true }
            val sdkProxy = ChannelHook::class.java.method("getCustomCpsChannel", objectArrayType)
                .apply { isAccessible = true }
            val lookups = listOf("getDeclaredMethod", "getMethod").map { name ->
                Class::class.java.method(name, String::class.java, classArrayType)
            }

            lookups.forEach { lookup ->
                registrations.add(checkNotNull(lookup.hook {
                    before {
                        val requestedName = args[0] as? String ?: return@before
                        if (requestedName != "getChannel" && requestedName != "getCustomCpsChannel") {
                            return@before
                        }
                        val ownerName = (thisObject as? Class<*>)?.name ?: return@before
                        val parameterTypes = args[1] as? Array<*> ?: return@before
                        if (parameterTypes.size != 1) return@before

                        val proxy = when {
                            requestedName == "getChannel" &&
                                    ownerName == APK_CHANNELING_CLASS &&
                                    parameterTypes[0] == File::class.java -> apkProxy

                            requestedName == "getCustomCpsChannel" &&
                                    ownerName.startsWith(SDK_CLASS_PREFIX) &&
                                    parameterTypes[0] == objectArrayType -> sdkProxy

                            else -> return@before
                        }

                        result = proxy
                    }
                }))
            }
            installed = true
        } catch (error : Throwable) {
            for (registration in registrations.asReversed()) {
                runCatching { registration.unhook() }
            }
        }
    }

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun getChannel(apk: File?): String = CHANNEL

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun getCustomCpsChannel(arguments: Array<Any?>?): String = CHANNEL
}
