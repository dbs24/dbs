package org.dbs.ref.serv.enums.convert

import org.dbs.ref.serv.enums.TransactionKindEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class TransactionKindConverter : Converter<Int, TransactionKindEnum> {
    override fun convert(transactionKindId: Int) = TransactionKindEnum.getEnum(transactionKindId)
}
