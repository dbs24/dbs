package org.dbs.spring.ref

import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import java.io.Serializable
import java.util.concurrent.atomic.AtomicBoolean

@Suppress(UNCHECKED_CAST)
abstract class AbstractRefEntity<T> : Persistable<T>, Serializable {

    @Transient
    val internalNew = AtomicBoolean(false)
    fun <E : AbstractRefEntity<T>> asNew() = also {
        internalNew.set(true)
    } as E

    override fun isNew() = internalNew.get()

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 2L
    }

}
