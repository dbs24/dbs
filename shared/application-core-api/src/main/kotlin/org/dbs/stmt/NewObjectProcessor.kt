package org.dbs.stmt

@Deprecated("Java anachronism. Should be replaced")
fun interface NewObjectProcessor<T> {
    @Throws(Throwable::class)
    fun initialize(t: T)
}
