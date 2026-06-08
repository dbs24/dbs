package org.dbs.ext

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LateInitProperty<T : Any> : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    fun isInitialized(): Boolean = value != null

    fun isNotInitialized(): Boolean = !isInitialized()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw UninitializedPropertyAccessException("Property ${property.name} is not initialized")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val current = this.value
        require(current == null) { "Property ${property.name} is already initialized with: $current" }
        this.value = value
    }
}

fun <T : Any> lateInitProperty() = LateInitProperty<T>()
