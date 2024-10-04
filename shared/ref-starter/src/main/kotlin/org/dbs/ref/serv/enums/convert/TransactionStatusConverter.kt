package org.dbs.ref.serv.enums.convert

import org.dbs.ref.serv.enums.TransactionStatusEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class TransactionStatusConverter : Converter<Int, TransactionStatusEnum> {
    override fun convert(transactionStatusId: Int) = TransactionStatusEnum.getEnum(transactionStatusId)
}
