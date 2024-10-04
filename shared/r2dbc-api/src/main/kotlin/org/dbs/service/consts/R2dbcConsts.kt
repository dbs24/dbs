package org.dbs.service.consts

object R2dbcConsts {

    object SqlConsts {

        const val SQL_COUNT_ALL = """
            SELECT COUNT(*)
            """

        const val SQL_PAGEABLE_CLAUSE = """
           LIMIT :#{#pageable.pageSize}
           OFFSET :#{#pageable.offset}            
        """

    }

    fun String.likeBegin() = "${this.trim().lowercase()}%"
    fun String.likeEnds() = "%${this.trim().lowercase()}"
    fun String.likeContain() = "%${this.trim().lowercase()}%"
    fun String.likeStartWord() = " %${this.trim().lowercase()}%"


}
