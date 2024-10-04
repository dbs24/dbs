package org.dbs.ref.serv.enums

import org.dbs.consts.TransactionKindCode
import org.dbs.consts.TransactionKindId
import org.dbs.consts.TransactionKindName
import org.dbs.exception.UnknownEnumException

enum class TransactionKindEnum(
    private val transactionKindId: TransactionKindId,
    private val transactionKindName: TransactionKindName,
    private val transactionKindCode: TransactionKindCode
) {
    INTERNAL_TRANSACTION(234000, "Internal transaction", "INTERNAL_TRANSACTION"),
    WITHDRAWAL_TO_BPC(234001, "Withdrawal to a bank payment card", "WITHDRAWAL_TO_BPC"),
    DEPOSITING_MONEY_WITH_BPC(234002, "Depositing money with a bank payment card", "DEPOSITING_MONEY_WITH_BPC"),
    DEPOSITING_MONEY_FROM_OTHER(234003,  "Depositing money from another service", "DEPOSITING_MONEY_FROM_OTHER"),
    WITHDRAWAL_TO_OTHER(234004,  "Withdrawal to another service", "WITHDRAWAL_TO_OTHER"),
    INTERNAL_TRANSACTION_INCOME(234005, "Internal transaction income", "INTERNAL_TRANSACTION_INCOME"),
    INTERNAL_TRANSACTION_RELEASE(234006, "Internal transaction release", "INTERNAL_TRANSACTION_RELEASE"),
    ;

    companion object {
        //==================================================================================================================
        //@get:Synchronized

        fun getEnum(transactionKindId: TransactionKindId): TransactionKindEnum =
            entries.find { it.transactionKindId == transactionKindId}
                ?: throw UnknownEnumException("Transaction kind id not found ($transactionKindId)")

        fun getEnum(transactionKindCode: TransactionKindCode): TransactionKindEnum =
            entries.find { it.transactionKindCode == transactionKindCode }
                ?: throw UnknownEnumException("Transaction kind code not found ($transactionKindCode)")


        fun isExistEnum(transactionKindCode: TransactionKindCode) = entries
            .any { it.transactionKindCode == transactionKindCode }

        fun isExistEnum(id: TransactionKindId) = entries.any { it.transactionKindId == id }

        val transactionKindNames by lazy { entries.map(TransactionKindEnum::transactionKindName) }

    }

    fun getTransactionKindName() = this.transactionKindName

    fun getTransactionKindCode() = this.transactionKindCode

    fun getTransactionKindId() = this.transactionKindId
}
