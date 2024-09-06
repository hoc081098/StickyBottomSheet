package com.hoc081098.stickybottomsheet

import android.app.Activity
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

object ActivityComponents {
    private var components = mutableMapOf<Activity, Any>()

    fun <T : Any> get(activity: Activity, clazz: KClass<T>): T? =
        components[activity].let { clazz.safeCast(it) }

    fun <T : Any> require(activity: Activity, clazz: KClass<T>): T =
        get(activity, clazz) ?: error("No component for $activity")

    fun remove(activity: Activity) {
        components.remove(activity)
        println(">>> Removed component for $activity")
    }

    fun <T : Any> put(activity: Activity, component: T) {
        components[activity] = component
        println(">>> Put component for $activity: $component")
    }
}