package org.dbs.ref.serv.service

import org.dbs.application.core.service.funcs.Patterns.STATUS_CODE_PATTERN
import org.dbs.ref.serv.enums.TransactionStatusEnum

object AccountTransactionStatusFuncs {
    fun String.isValidTransactionStatusCode() =
        STATUS_CODE_PATTERN.matcher(this).matches() && TransactionStatusEnum.isExistEnum(this)
}
