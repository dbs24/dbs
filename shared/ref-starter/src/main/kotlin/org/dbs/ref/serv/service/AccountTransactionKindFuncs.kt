package org.dbs.ref.serv.service

import org.dbs.application.core.service.funcs.Patterns.KIND_CODE_PATTERN
import org.dbs.ref.serv.enums.TransactionKindEnum

object AccountTransactionKindFuncs {
    fun String.isValidTransactionKindCode() =
        KIND_CODE_PATTERN.matcher(this).matches() && TransactionKindEnum.isExistEnum(this)
}
