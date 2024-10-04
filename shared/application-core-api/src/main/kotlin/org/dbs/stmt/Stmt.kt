package org.dbs.stmt

@Deprecated("Java anachronism. Should be replaced")
fun interface Stmt<T> {
    @Throws(Throwable::class)
    fun executeStmt(): T
}
