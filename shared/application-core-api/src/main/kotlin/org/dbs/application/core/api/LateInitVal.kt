package org.dbs.application.core.api

import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.UNCHECKED_CAST
import java.io.Closeable
import org.dbs.consts.SysConst.NOT_ASSIGNED

object EMPTYOBJECT

@JvmInline
value class LateInitVal<T>(private val propertyStorage: InternalPropertyStorageImpl<T>) :
    InternalPropertyStorage<T> by propertyStorage {
    @Suppress(UNCHECKED_CAST)
    constructor() : this(InternalPropertyStorageImpl(NOT_ASSIGNED, EMPTYOBJECT as T))

    @Suppress(UNCHECKED_CAST)
    constructor(initValue: T) : this(InternalPropertyStorageImpl(NOT_ASSIGNED, initValue))

    @Suppress(UNCHECKED_CAST)
    constructor(propertyName: String) : this(InternalPropertyStorageImpl(propertyName, EMPTYOBJECT as T))
}

class LateInitValNoInline<T>(private val propertyStorage: InternalPropertyStorageImpl<T>) : Closeable,
    InternalPropertyStorage<T> by propertyStorage {

    @Suppress(UNCHECKED_CAST)
    constructor() : this(InternalPropertyStorageImpl(NOT_ASSIGNED, EMPTYOBJECT as T))

    @Suppress(UNCHECKED_CAST)
    constructor(initValue: T) : this(InternalPropertyStorageImpl(NOT_ASSIGNED, initValue))

    @Suppress(UNCHECKED_CAST)
    constructor(propertyName: String) : this(InternalPropertyStorageImpl(propertyName, EMPTYOBJECT as T))

}

@JvmInline
value class CollectionLateInitVal<T>(private val propertyStorage: InternalPropertyStorageImpl<Collection<T>>) :
    InternalPropertyStorage<Collection<T>> by propertyStorage {

    @Suppress(UNCHECKED_CAST)
    constructor() : this(InternalPropertyStorageImpl(NOT_ASSIGNED, createCollection<T>()))

    @Suppress(UNCHECKED_CAST)
    constructor(value: Collection<T>) : this(InternalPropertyStorageImpl(NOT_ASSIGNED, value))

    fun addAll(elements: Collection<T>): Boolean = if (isNotInitialized()) {
        init(elements); true
    } else (value as MutableCollection).addAll(elements)

    override fun getDefaultValue(): Collection<T> = if (isNotInitialized()) {
        createCollection<T>().also { init(it) }
    } else {
        propertyStorage.value
    }
}


sealed interface InternalPropertyStorage<T> : Closeable {
    fun init(assignedValue: T): T
    fun update(assignedValue: T): T
    fun invalidate()
    fun isNotInitialized(): Boolean
    fun isInitialized(): Boolean
    val value: T
    val valueOrNull: T?
    fun valueOrDefault(defaultValue: T): T
    override fun close() = invalidate()
    fun raiseNotInitializedException(): T
    fun getDefaultValue(): T
}

class InternalPropertyStorageImpl<T>(
    private val propertyName: String = NOT_ASSIGNED,
    @Suppress(UNCHECKED_CAST)
    private var readOnlyValue: T = EMPTYOBJECT as T
) : InternalPropertyStorage<T>, Closeable {

    //==================================================================================================================
    override fun init(assignedValue: T): T = assignedValue.also {
        require(readOnlyValue === EMPTYOBJECT) { "Property '${propertyName}' already initialized [${readOnlyValue}], newValue=[$assignedValue]" }
        readOnlyValue = it
    }

    override fun update(assignedValue: T): T = assignedValue.also {
        readOnlyValue = assignedValue
    }

    @Suppress(UNCHECKED_CAST)
    override fun invalidate() {
        readOnlyValue = EMPTYOBJECT as T
    }

    override fun isNotInitialized() = (readOnlyValue === EMPTYOBJECT)

    override fun isInitialized() = (readOnlyValue != EMPTYOBJECT)

    @Suppress(UNCHECKED_CAST)
    override val value: T
        get() = readOnlyValue.let {
            it.takeIf { isInitialized() } ?: getDefaultValue()
        }.also {
            require(it != EMPTYOBJECT) { "Property '${propertyName}' is not initialized" }
        }

    @Suppress(UNCHECKED_CAST)
    override val valueOrNull: T?
        get() = readOnlyValue.takeIf { isInitialized() }

    @Suppress(UNCHECKED_CAST)
    override fun valueOrDefault(defaultValue: T): T =
        (readOnlyValue.takeIf { isInitialized() } ?: defaultValue)

    override fun toString() = value.toString()

    override fun raiseNotInitializedException(): T =
        error("Property ${"'$propertyName'".takeUnless { it == EMPTY_STRING } ?: EMPTY_STRING}" +
                "[${javaClass.simpleName}<${this.logTag()}>] is not initialized ")

    private inline fun <reified T> T.logTag() = T::class.typeParameters.first()

    override fun getDefaultValue(): T = raiseNotInitializedException()

}