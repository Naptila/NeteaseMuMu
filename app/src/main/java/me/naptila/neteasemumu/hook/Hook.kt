package me.naptila.neteasemumu.hook

abstract class Hook {
    abstract fun hook(classLoader: ClassLoader)
}