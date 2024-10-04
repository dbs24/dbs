package org.dbs.ref.serv.enums.convert

import org.dbs.ref.serv.enums.TransactionStatusEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class TransactionStatusEnumConverter : Converter<TransactionStatusEnum, Int> {
    override fun convert(transactionStatusEnum: TransactionStatusEnum) = transactionStatusEnum.getTransactionStatusId()
}
