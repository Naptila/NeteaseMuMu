package me.naptila.neteasemumu.hook.builder

import android.util.Log
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError
import de.robv.android.xposed.XposedHelpers.findClass

fun String.toClass(classLoader: ClassLoader): Class<*> = try {
    findClass(this, classLoader)
} catch (e: ClassNotFoundError) {
    log(Log.getStackTraceString(e))
    throw e
} catch (e: ClassNotFoundException) {
    log(Log.getStackTraceString(e))
    throw e
}