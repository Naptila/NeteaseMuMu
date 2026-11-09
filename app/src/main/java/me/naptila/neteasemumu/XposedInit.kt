package me.naptila.neteasemumu

import android.content.res.Resources
import android.content.res.XModuleResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import me.naptila.neteasemumu.hook.hooks.ChannelHook
import me.naptila.neteasemumu.hook.hooks.SystemHook

class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
        moduleRes = getModuleRes(modulePath)
    }

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.appInfo == null)
            return

        SystemHook.hook(lpparam.classLoader)
        ChannelHook.hook(lpparam.classLoader)
    }

    companion object {
        lateinit var modulePath: String
        lateinit var moduleRes: Resources

        @JvmStatic
        fun getModuleRes(path: String): Resources {
            return XModuleResources.createInstance(path, null)
        }
    }
}
