package me.naptila.neteasemumu.hook.builder

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.findField
import java.lang.reflect.Field

fun Class<*>.field(
    field: String?
): Pair<Field, Any?> = Pair(findField(this, field), null)

fun Any.field(
    field: String?
): Pair<Field, Any?> = Pair(findField(this.javaClass, field), this)

fun <T> Pair<Field, Any?>.setValue(value: T) = apply {
    try {
        when (value) {
            is Boolean -> first.setBoolean(second, value)
            is Float -> first.setFloat(second, value)
            is Int -> first.setInt(second, value)
            is Long -> first.setLong(second, value)
            is Double -> first.setDouble(second, value)
            is Byte -> first.setByte(second, value)
            is Char -> first.setChar(second, value)
            else -> first.set(second, value)
        }
    } catch (e: IllegalAccessException) {
        XposedBridge.log(e)
        throw IllegalAccessError(e.message)
    } catch (e: IllegalArgumentException) {
        throw e
    }
}

fun Pair<Field, Any?>.getValue(): Any? = try {
    first.get(second)
} catch (e: IllegalAccessException) {
    // should not happen
    XposedBridge.log(e)
    throw IllegalAccessError(e.message)
} catch (e: IllegalArgumentException) {
    throw e
}

fun <T> Pair<Field, Any?>.getValueAs() = getValue() as T

fun Pair<Field, Any?>.boolean() = first.getBoolean(second)

fun Pair<Field, Any?>.float() = first.getFloat(second)

fun Pair<Field, Any?>.int() = first.getInt(second)

fun Pair<Field, Any?>.long() = first.getLong(second)

fun Pair<Field, Any?>.double() = first.getDouble(second)

fun Pair<Field, Any?>.byte() = first.getByte(second)

fun Pair<Field, Any?>.char() = first.getChar(second)