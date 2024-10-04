package org.dbs.ref.serv.enums.convert

import org.dbs.ref.serv.enums.TransactionKindEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class TransactionKindEnumConverter : Converter<TransactionKindEnum, Int> {
    override fun convert(transactionKindEnum: TransactionKindEnum) = transactionKindEnum.getTransactionKindId()
}
