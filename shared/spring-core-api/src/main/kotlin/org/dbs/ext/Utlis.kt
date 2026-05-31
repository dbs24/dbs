package org.dbs.ext

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T : Any> lateInitProperty(): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return value ?: throw UninitializedPropertyAccessException("Property ${property.name} is not initialized")
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        val current = this.value
        require(current == null) { "Property ${property.name} is already initialized with: $current" }
        this.value = value
    }
}