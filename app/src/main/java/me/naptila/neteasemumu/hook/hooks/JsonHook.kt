package me.naptila.neteasemumu.hook.hooks

import me.naptila.neteasemumu.hook.Hook
import me.naptila.neteasemumu.hook.builder.hook
import me.naptila.neteasemumu.hook.builder.method
import me.naptila.neteasemumu.hook.builder.toClass
import org.json.JSONArray

/**
 * 老HOOK方案
 */
object JsonHook : Hook() {
    override fun hook(classLoader: ClassLoader) {
        "org.json.JSONObject".toClass(classLoader).apply {
            method(
                "optBoolean",
                String::class.java,
                Boolean::class.java
            ).hook {
                before {
                    if (args[0]?.equals("select_platform") == true) {
                        result = true
                    }
                }
            }

            method(
                "optJSONArray",
                String::class.java
            ).hook {
                before {
                    if (args[0]?.equals("select_platforms") == true) {
                        result = JSONArray(intArrayOf(1, 2, 8))
                    }
                }
            }
        }
    }
}