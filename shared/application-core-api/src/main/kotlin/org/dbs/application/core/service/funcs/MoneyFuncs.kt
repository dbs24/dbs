package org.dbs.application.core.service.funcs

import org.dbs.consts.Money
import org.dbs.consts.MoneyNull
import org.dbs.consts.SysConst.MONEY_ZERO
import java.math.RoundingMode.HALF_UP

object MoneyFuncs {

    fun MoneyNull.isEqual(comparable: MoneyNull) =
        ((comparable ?: MONEY_ZERO).compareTo(this ?: MONEY_ZERO) == 0)

    fun MoneyNull.isGreater(comparable: MoneyNull) =
        ((comparable ?: MONEY_ZERO).compareTo(this ?: MONEY_ZERO) == -1)

    fun MoneyNull.isPositive() =
        ((this ?: MONEY_ZERO).compareTo(MONEY_ZERO) == 1)

    fun MoneyNull.isEqualOrGreater(comparable: MoneyNull) =
        ((comparable ?: MONEY_ZERO).compareTo(this ?: MONEY_ZERO) == 0) ||
                ((comparable ?: MONEY_ZERO).compareTo(this ?: MONEY_ZERO) == -1)

    fun Money.roundHalfUp() = setScale(2, HALF_UP)
}
