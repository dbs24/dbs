package org.dbs.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LateInitProperty<T : Any>(val propertyName: String = "") : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    fun isInitialized(): Boolean = value != null

    fun isNotInitialized(): Boolean = !isInitialized()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw UninitializedPropertyAccessException("Property ${property.name} is not initialized " +
                if (propertyName.isNotEmpty()) propertyName else ""
        )
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val current = this.value
        require(current == null) { "Property ${property.name} " +
                "${propertyName.ifEmpty { "" }} is already initialized with: $current" }
        this.value = value
    }
}

fun <T : Any> lateInitProperty(propertyName: String = "") = LateInitProperty<T>(propertyName)
