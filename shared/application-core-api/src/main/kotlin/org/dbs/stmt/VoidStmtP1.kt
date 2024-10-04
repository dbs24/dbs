package org.dbs.stmt

@Deprecated("Java anachronism. Should be replaced")
fun interface VoidStmtP1<T> {
    @Throws(Throwable::class)
    fun executeStmt(t: T)
}
