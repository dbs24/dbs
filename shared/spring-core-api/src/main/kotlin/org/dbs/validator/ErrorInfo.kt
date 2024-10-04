package org.dbs.validator

import org.dbs.validator.Field.GENERAL_FIELD


data class ErrorInfo(val error: Error, val field: Field, val errorMsg: String) {

    companion object {
        fun create(error: Error, field: Field, errorMsg: String): ErrorInfo = ErrorInfo(error, field, errorMsg)
        fun create(error: Error, errorMsg: String): ErrorInfo = ErrorInfo(error, GENERAL_FIELD, errorMsg)

        @Deprecated(" use 'responseBody.errors.whenNoErrors' expression ")
        val noErrors = MutableCollection<ErrorInfo>::isEmpty
        val noCollectionsErrors = Collection<ErrorInfo>::isEmpty
    }
}
