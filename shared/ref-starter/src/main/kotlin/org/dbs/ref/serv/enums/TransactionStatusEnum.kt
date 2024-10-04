package org.dbs.ref.serv.enums

import org.dbs.consts.TransactionStatusCode
import org.dbs.consts.TransactionStatusId
import org.dbs.consts.TransactionStatusName
import org.dbs.exception.UnknownEnumException

enum class TransactionStatusEnum(
    private val transactionStatusId: TransactionStatusId,
    private val transactionStatusName: TransactionStatusName,
    private val transactionStatusCode: TransactionStatusCode
) {
    ACTUAL(123000, "Actual", "ACTUAL"),
    REJECTED_INSUFFICIENT_FUNDS(123001, "Insufficient funds", "REJECTED_INSUFFICIENT_FUNDS"),
    REJECTED_ACCOUNT_IS_BLOCKED(123002, "Blocked", "REJECTED_ACCOUNT_IS_BLOCKED"),
    REJECTED_ACCESS_DENIED(123003,  "Access denied", "REJECTED_ACCESS_DENIED"),
    ;

    companion object {
        //==================================================================================================================
        //@get:Synchronized

        fun getEnum(transactionStatusId: TransactionStatusId): TransactionStatusEnum =
            entries.find { it.transactionStatusId == transactionStatusId}
                ?: throw UnknownEnumException("Transaction status id not found ($transactionStatusId)")

        fun getEnum(transactionStatusName: TransactionStatusName): TransactionStatusEnum =
            entries.find { it.transactionStatusName == transactionStatusName }
                ?: throw UnknownEnumException("Transaction status name not found ($transactionStatusName)")

        fun isExistEnumByName(transactionStatusName: TransactionStatusName) = entries
            .any { it.transactionStatusName == transactionStatusName }

        fun isExistEnum(transactionStatusCode: TransactionStatusCode) = entries
            .any { it.transactionStatusCode == transactionStatusCode }

        fun isExistEnum(id: TransactionStatusId) = entries.any { it.transactionStatusId == id }

        val transactionStatusNames by lazy { entries.map(TransactionStatusEnum::transactionStatusName) }

    }

    fun getTransactionStatusName() = this.transactionStatusName

    fun getTransactionStatusId() = this.transactionStatusId


    fun getTransactionStatusCode() = this.transactionStatusCode
}
