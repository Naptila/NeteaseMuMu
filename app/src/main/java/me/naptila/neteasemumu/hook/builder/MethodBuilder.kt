package me.naptila.neteasemumu.hook.builder

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge.*
import de.robv.android.xposed.XposedHelpers.*
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Member
import java.lang.reflect.Method

typealias BeforeHooker = MethodHookParam.() -> Unit
typealias AfterHooker = MethodHookParam.() -> Unit
typealias Replacer = MethodHookParam.() -> Any?

interface MethodHook {
    fun before(hooker: BeforeHooker)

    fun after(hooker: AfterHooker)

    fun unhook()
}

interface MethodReplace {
    fun replace(replacer: Replacer)

    fun unhook()
}

class MethodReplaceImpl(private var method: Member) : MethodReplace {
    private var replacer: Replacer? = null
    private var unhook: XC_MethodHook.Unhook? = null

    override fun replace(replacer: Replacer) {
        this.replacer = replacer
    }

    override fun unhook() {
        unhook?.unhook()
    }

    fun replace(): XC_MethodHook.Unhook? {
        unhook = hookMethod(method, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) {
                try {
                    replacer?.let { it(param) }
                } catch (e: Throwable) {
                    log("Error occurred calling replacer on $method")
                    log(Log.getStackTraceString(e))
                }
            }
        })
        return unhook
    }
}

class MethodHookImpl(private var method: Member) : MethodHook {
    private var beforeHooker: BeforeHooker? = null
    private var afterHooker: AfterHooker? = null
    private var unhook: XC_MethodHook.Unhook? = null

    override fun before(hooker: BeforeHooker) {
        this.beforeHooker = hooker
    }

    override fun after(hooker: AfterHooker) {
        this.afterHooker = hooker
    }

    override fun unhook() {
        unhook?.unhook()
    }

    fun hook(): XC_MethodHook.Unhook? {
        unhook = hookMethod(method, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    beforeHooker?.let { it(param) }
                } catch (e: Throwable) {
                    log("Error occurred calling before hooker on $method")
                    log(Log.getStackTraceString(e))
                }
            }

            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    afterHooker?.let { it(param) }
                } catch (e: Throwable) {
                    log("Error occurred calling after hooker on $method")
                    log(Log.getStackTraceString(e))
                }
            }
        })

        return unhook
    }
}

fun Class<*>.constructor(
    vararg args: Any?
): Constructor<*> = findConstructorExact(this, *args)

fun Class<*>.method(
    method: String?,
    vararg args: Any?
): Method = findMethodExact(this, method, *args)

fun Any.method(
    method: String?,
    vararg args: Any?
): Pair<Method, Any> = Pair(findMethodBestMatch(this.javaClass, method, *args), this)

inline fun Class<*>.allConstructors(isAccessible: Boolean = true, result: (index: Int, constructor: Constructor<*>) -> Unit) =
    declaredConstructors.forEachIndexed { p, it -> result(p, it.also { e -> e.isAccessible = isAccessible }) }

inline fun Class<*>.allMethods(isAccessible: Boolean = true, result: (index: Int, method: Method) -> Unit) =
    declaredMethods.forEachIndexed { p, it -> result(p, it.also { e -> e.isAccessible = isAccessible }) }

inline fun Member.hook(
    crossinline hooker: MethodHook.() -> Unit,
) = MethodHookImpl(this).apply(hooker).hook()

inline fun Member.replace(
    crossinline replacer: MethodReplace.() -> Unit,
) = MethodReplaceImpl(this).apply(replacer).replace()

fun Method.call(vararg args: Any?): Any? = try {
    this.invoke(null, *args)
} catch (e: IllegalAccessException) {
    // should not happen
    log(e)
    throw IllegalAccessError(e.message)
} catch (e: IllegalArgumentException) {
    throw e
} catch (e: InvocationTargetException) {
    throw InvocationTargetError(e.cause)
}

fun <T> Method.callAs(vararg args: Any?): Any? = call(*args) as T

fun Pair<Method, Any>.call(vararg args: Any?): Any? = try {
    first.invoke(second, *args)
} catch (e: IllegalAccessException) {
    // should not happen
    log(e)
    throw IllegalAccessError(e.message)
} catch (e: IllegalArgumentException) {
    throw e
} catch (e: InvocationTargetException) {
    throw InvocationTargetError(e.cause)
}

fun <T> Pair<Method, Any>.callAs(vararg args: Any?) = call(*args) as T

class InvocationTargetError(cause: Throwable?) : Error(cause) {
    companion object {
        private const val serialVersionUID = -1070936889459514628L
    }
}