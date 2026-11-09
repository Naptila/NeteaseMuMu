package me.naptila.neteasemumu.hook.hooks

import me.naptila.neteasemumu.hook.Hook
import me.naptila.neteasemumu.hook.builder.hook
import me.naptila.neteasemumu.hook.builder.method
import me.naptila.neteasemumu.hook.builder.toClass

/**
 * 修改ro.build.id，伪装是MuMu模拟器，W528JS也行。
 */
object SystemHook : Hook() {
    override fun hook(classLoader: ClassLoader) {
        "android.os.SystemProperties".toClass(classLoader).method(
            "get",
            String::class.java,
            String::class.java
        ).hook {
            before {
                val key: String = args[0].toString()

                if (key == "ro.build.id") {
                    result = "V417IR"
                }
            }
        }
    }
}