package org.dbs.stmt

@Deprecated("Java anachronism. Should be replaced")
fun interface VoidStmt<T> {
    @Throws(Throwable::class)
    fun executeStmt(): Unit
}
