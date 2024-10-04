package org.dbs.rest.api.nio

import org.dbs.application.core.service.funcs.Patterns.EMAIL_PATTERN
import org.dbs.application.core.service.funcs.Patterns.JWT_PATTERN
import org.dbs.application.core.service.funcs.StringFuncs.first15
import org.dbs.application.core.service.funcs.StringFuncs.ifNotEmpty
import org.dbs.consts.Collection2Unit
import org.dbs.consts.CurrencyCode
import org.dbs.consts.GenericArg2Unit
import org.dbs.consts.Int2Unit
import org.dbs.consts.Jwt
import org.dbs.consts.Long2Unit
import org.dbs.consts.Money
import org.dbs.consts.Money2Unit
import org.dbs.consts.MoneyNull
import org.dbs.consts.NoArg2Unit
import org.dbs.consts.OperDateDto
import org.dbs.consts.OperDateDtoNull
import org.dbs.consts.String2Unit
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.INTEGER_ONE
import org.dbs.consts.SysConst.INTEGER_ZERO
import org.dbs.consts.SysConst.LONG_ZERO
import org.dbs.consts.SysConst.MAX_DATE_TIME_LONG
import org.dbs.consts.SysConst.MAX_MONEY
import org.dbs.consts.SysConst.MIN_DATE_TIME_LONG
import org.dbs.enums.I18NEnum.FLD_INVALID_BIG_DECIMAL_DIGITS
import org.dbs.enums.I18NEnum.FLD_INVALID_BIG_DECIMAL_VALUE
import org.dbs.enums.I18NEnum.FLD_INVALID_FIELD_LENGTH
import org.dbs.enums.I18NEnum.FLD_INVALID_FIELD_VALUE_4_PATTERN
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_COUNTRY
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_CURRENCY
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_CURRENCY_ISO
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_REGION
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_TRANSACTION_KIND_CODE
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_TRANSACTION_STATUS_CODE
import org.dbs.ref.serv.enums.CurrencyEnum
import org.dbs.ref.serv.enums.GenderEnum
import org.dbs.ref.serv.service.AccountTransactionKindFuncs.isValidTransactionKindCode
import org.dbs.ref.serv.service.AccountTransactionStatusFuncs.isValidTransactionStatusCode
import org.dbs.ref.serv.service.CountryFuncs.isValidCountryCode
import org.dbs.ref.serv.service.CurrencyFuncs.isValidCurrencyCode
import org.dbs.ref.serv.service.RegionFuncs.isValidRegionCode
import org.dbs.rest.api.enums.RestOperCodeEnum
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Error.INVALID_ENTITY_STATUS
import org.dbs.validator.Field
import java.util.regex.Pattern
import kotlin.Int.Companion.MAX_VALUE

@Suppress("IMPLICIT_CAST_TO_ANY")
object FieldValidators {

    fun <T : ResponseDto> HttpResponseBody<T>.validateNullableField(
        value: String?,
        restOperCodeEnum: RestOperCodeEnum,
        pattern: Pattern,
        field: Field
    ) = value.ifNotEmpty { validateNullableField(value, restOperCodeEnum, pattern, field) {} }

    fun <T : ResponseDto> HttpResponseBody<T>.validateNullableField(
        value: String?,
        restOperCodeEnum: RestOperCodeEnum,
        pattern: Pattern,
        field: Field,
        successFunc: String2Unit
    ) = value.ifNotEmpty { value?.let { validateField(it, restOperCodeEnum, pattern, field, successFunc) } }

    fun <T : ResponseDto> HttpResponseBody<T>.validateNonEmptyField(
        value: String?,
        restOperCodeEnum: RestOperCodeEnum,
        pattern: Pattern,
        field: Field
    ) = validateNonEmptyField(value ?: EMPTY_STRING, INTEGER_ONE, restOperCodeEnum, pattern, field) {}

    fun <T : ResponseDto> HttpResponseBody<T>.validateNonEmptyField(
        value: String?,
        restOperCodeEnum: RestOperCodeEnum,
        pattern: Pattern,
        field: Field,
        successFunc: String2Unit
    ) = validateNonEmptyField(value ?: EMPTY_STRING, INTEGER_ONE, restOperCodeEnum, pattern, field, successFunc)

    fun <T : ResponseDto> HttpResponseBody<T>.validateGender(
        gender: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field
    ) = validateGender(gender, restOperCodeEnum, field) {}

    fun <T : ResponseDto> HttpResponseBody<T>.validateGender(
        gender: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field,
        func: String2Unit
    ) = run {
        if (GenderEnum.isExistsEnum(gender.trim().uppercase()))
            func(gender)
        else run {
            addErrorInfo(
                restOperCodeEnum,
                INVALID_ENTITY_ATTR,
                field,
                "unknown gender value: '$gender'"
            )
        }
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validateEmail(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field
    ) =
        validateNonEmptyField(value, restOperCodeEnum, EMAIL_PATTERN, field) {}

    fun <T : ResponseDto> HttpResponseBody<T>.validateNullableEmail(
        value: String?,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field
    ) =
        value?.let { validateEmail(it, restOperCodeEnum, field) }

    @Deprecated("use appropriate pattern")
    private inline fun <T : ResponseDto> HttpResponseBody<T>.validateNonEmptyField(
        value: String,
        minLength: Int,
        restOperCodeEnum: RestOperCodeEnum,
        pattern: Pattern,
        field: Field,
        successFunc: String2Unit
    ) {
        validateField(value, restOperCodeEnum, pattern, field) {
            if (value.length < minLength) {
                responseEntity.apply {
                    //responseCode = restOperCodeEnum
                    addErrorInfo(
                        restOperCodeEnum,
                        INVALID_ENTITY_ATTR,
                        field,
                        "${field.name}: ${findI18nMessage(FLD_INVALID_FIELD_LENGTH)}: " +
                                "(${value.length}<$minLength)"
                    )
                }
            } else {
                successFunc(value)
            }
        }
    }

    private fun <T : ResponseDto> HttpResponseBody<T>.validateField(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        pattern: Pattern,
        field: Field
    ) {
        validateField(value, restOperCodeEnum, pattern, field) {}
    }

    private inline fun <T : ResponseDto> HttpResponseBody<T>.validateField(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        pattern: Pattern,
        field: Field,
        successFunc: String2Unit
    ) {
        if (!pattern.matcher(value).matches()) {
            responseEntity.apply {
                //responseCode = restOperCodeEnum
                logger.warn(
                    "${field.name}: ${
                        findI18nMessage(FLD_INVALID_FIELD_VALUE_4_PATTERN)
                    } '${pattern.pattern()}': '$value'"
                )
                addErrorInfo(
                    restOperCodeEnum,
                    INVALID_ENTITY_ATTR, field,
                    "${field.name}: ${
                        findI18nMessage(FLD_INVALID_FIELD_VALUE_4_PATTERN)
                    } '${getPatternString(pattern)}': '$value'"
                )
            }
        } else {
            successFunc(value)
        }
    }


    fun <T : ResponseDto> HttpResponseBody<T>.validateNullableMoney(
        value: MoneyNull,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field
    ) =
        value?.let { validateMoney(value, restOperCodeEnum, field) }


    fun <T : ResponseDto> HttpResponseBody<T>.validateMoney(
        value: Money,
        roc: RestOperCodeEnum,
        field: Field
    ) = validateMoney(value, roc, field) {}

    fun <T : ResponseDto> HttpResponseBody<T>.validateMoneyCommaDigit(
        value: Money,
        allowedCountDigitsAfterComma: Int,
        roc: RestOperCodeEnum,
        field: Field
    ) = run {
        val split = value.toString().split(".")
        if (split.size > 1) {
            val num = split[1].length
            if (num > allowedCountDigitsAfterComma) {
                responseEntity.apply {
                    //responseCode = roc
                    addErrorInfo(
                        roc, INVALID_ENTITY_ATTR, field,
                        "${field.name}: ${
                            findI18nMessage(FLD_INVALID_BIG_DECIMAL_DIGITS)
                        } $allowedCountDigitsAfterComma ($value)"
                    )
                }
            }
        }
    }

    private inline fun <T : ResponseDto> HttpResponseBody<T>.validateMoney(
        value: Money,
        roc: RestOperCodeEnum,
        field: Field,
        func: Money2Unit
    ) = if (!((value > Money.ZERO) && (value < MAX_MONEY))) {
        responseEntity.apply {
            //responseCode = roc
            addErrorInfo(
                roc, INVALID_ENTITY_ATTR, field, "${field.name}: ${
                    findI18nMessage(FLD_INVALID_BIG_DECIMAL_VALUE)
                } - [$value]"
            )
        }
    } else {
        func(value)
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validateCurrencyIso(
        value: CurrencyCode,
        roc: RestOperCodeEnum,
        field: Field
    ) = validateCurrencyIso(value, roc, field) {}

    private inline fun <T : ResponseDto> HttpResponseBody<T>.validateCurrencyIso(
        value: CurrencyCode,
        roc: RestOperCodeEnum,
        field: Field,
        func: String2Unit
    ) = if (!CurrencyEnum.isExistCurrency(value)) {
        responseEntity.apply {
            //responseCode = roc
            addErrorInfo(
                roc, INVALID_ENTITY_ATTR, field, "${field.name}: ${
                    findI18nMessage(FLD_UNKNOWN_CURRENCY_ISO)
                } ($value)"
            )
        }
    } else {
        func(value)
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validateJwt(
        value: Jwt,
        roc: RestOperCodeEnum,
        field: Field
    ) = validateJwt(value, roc, field) {}

    fun <T : ResponseDto> HttpResponseBody<T>.validateJwt(
        value: Jwt,
        roc: RestOperCodeEnum,
        field: Field,
        func: String2Unit
    ) = validateNonEmptyField(value, roc, JWT_PATTERN, field, func)

    fun <T : ResponseDto> HttpResponseBody<T>.validateAccTransactionStatus(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field
    ) = validateAccTransactionStatus(value, restOperCodeEnum, field) {}


    inline fun <T : ResponseDto> HttpResponseBody<T>.validateAccTransactionStatus(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field,
        func: String2Unit
    ) =
        if (!value.isValidTransactionStatusCode()) {
            responseEntity.apply {
                //responseCode = restOperCodeEnum
                addErrorInfo(
                    restOperCodeEnum, INVALID_ENTITY_ATTR, field, "${field.name}: " +
                            "${findI18nMessage(FLD_UNKNOWN_TRANSACTION_STATUS_CODE)} ($value)"
                )
            }
        } else {
            func(value)
        }

    fun <T : ResponseDto> HttpResponseBody<T>.validateAccTransactionKind(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field
    ) = validateAccTransactionKind(value, restOperCodeEnum, field) {}


    inline fun <T : ResponseDto> HttpResponseBody<T>.validateAccTransactionKind(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field,
        func: String2Unit
    ) =
        if (!value.isValidTransactionKindCode()) {
            responseEntity.apply {
                //responseCode = restOperCodeEnum
                addErrorInfo(
                    restOperCodeEnum, INVALID_ENTITY_ATTR, field, "${field.name}: " +
                            "${findI18nMessage(FLD_UNKNOWN_TRANSACTION_KIND_CODE)} ($value)"
                )
            }
        } else {
            func(value)
        }

    fun <T : ResponseDto> HttpResponseBody<T>.validateCurrency(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field
    ) = validateCurrency(value, restOperCodeEnum, field) {}


    inline fun <T : ResponseDto> HttpResponseBody<T>.validateCurrency(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field,
        func: String2Unit
    ) =
        if (!value.isValidCurrencyCode()) {
            responseEntity.apply {
                //responseCode = restOperCodeEnum
                addErrorInfo(
                    restOperCodeEnum, INVALID_ENTITY_ATTR, field, "${field.name}: " +
                            "${findI18nMessage(FLD_UNKNOWN_CURRENCY)} ($value)"
                )
            }
        } else {
            func(value)
        }

    fun <T : ResponseDto> HttpResponseBody<T>.validateRegion(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field
    ) =
        validateRegion(value, restOperCodeEnum, field) {}

    inline fun <T : ResponseDto> HttpResponseBody<T>.validateRegion(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field,
        func: String2Unit
    ) =
        if (!value.isValidRegionCode()) {
            responseEntity.apply {
                //responseCode = restOperCodeEnum
                addErrorInfo(
                    restOperCodeEnum, INVALID_ENTITY_ATTR, field, "${field.name}: " +
                            "${findI18nMessage(FLD_UNKNOWN_REGION)} ($value)"
                )
            }
        } else {
            func(value)
        }

    fun <T : ResponseDto> HttpResponseBody<T>.validateCountry(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field
    ) =
        validateCountry(value, restOperCodeEnum, field) {}

    inline fun <T : ResponseDto> HttpResponseBody<T>.validateCountry(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field,
        func: String2Unit
    ) =
        if (!value.isValidCountryCode()) {
            responseEntity.apply {
                //responseCode = restOperCodeEnum
                addErrorInfo(
                    restOperCodeEnum, INVALID_ENTITY_ATTR, field, "${field.name}: " +
                            "${findI18nMessage(FLD_UNKNOWN_COUNTRY)} ($value)"
                )
            }
        } else {
            func(value)
        }

    fun <T : ResponseDto> HttpResponseBody<T>.validateEntityStatus(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        items: List<String>,
        field: Field
    ) = validateEntityStatus(value, restOperCodeEnum, items, field) {}

    fun <T : ResponseDto> HttpResponseBody<T>.validateEntityStatus(
        value: String,
        restOperCodeEnum: RestOperCodeEnum,
        items: List<String>,
        field: Field,
        successFunc: String2Unit
    ) {
        val status = value.trim().uppercase()
        if (!items.contains(status)) {
            addErrorInfo(
                restOperCodeEnum,
                INVALID_ENTITY_STATUS,
                field,
                "${field.name}: Unknown entity status ($status)"
            )
        } else successFunc(status)
    }

    //===============================================================
    fun <T : ResponseDto> HttpResponseBody<T>.validateInteger(
        value: Int,
        roc: RestOperCodeEnum,
        field: Field
    ) {
        validateInteger(value, roc, field) {}
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validateInteger(
        value: Int,
        from: Int,
        to: Int,
        roc: RestOperCodeEnum,
        field: Field
    ) =
        validateInteger(value, from, to, roc, field) {}


    inline fun <T : ResponseDto> HttpResponseBody<T>.validateInteger(
        value: Int,
        roc: RestOperCodeEnum,
        field: Field,
        func: Int2Unit
    ) =
        if (!((value > INTEGER_ZERO) && (value < MAX_VALUE))) {
            addErrorInfo(
                roc,
                INVALID_ENTITY_ATTR,
                field,
                "${field.name}: Invalid integer non zero value - [$value]"
            )
        } else {
            func(value)
        }

    inline fun <T : ResponseDto> HttpResponseBody<T>.validateInteger(
        value: Int,
        from: Int,
        to: Int,
        roc: RestOperCodeEnum,
        field: Field,
        func: Int2Unit
    ) =
        if (!((value > from) && (value < to))) {
            addErrorInfo(
                roc,
                INVALID_ENTITY_ATTR,
                field,
                "${field.name}: Invalid integer value - [$value] <> [$from , $to]"
            )
        } else {
            func(value)
        }

    //===============================================================
    fun <T : ResponseDto> HttpResponseBody<T>.validateLong(
        value: Long,
        roc: RestOperCodeEnum,
        field: Field
    ) = validateLong(value, roc, field) {}

    fun <T : ResponseDto> HttpResponseBody<T>.validateNullableLong(
        value: Long?,
        roc: RestOperCodeEnum,
        field: Field
    ) = value?.let { validateLong(it, roc, field) {} }

    fun <T : ResponseDto> HttpResponseBody<T>.validateDateTime(
        value: OperDateDto,
        roc: RestOperCodeEnum,
        field: Field
    ) = validateLong(value, MIN_DATE_TIME_LONG, MAX_DATE_TIME_LONG, roc, field) {}

    fun <T : ResponseDto> HttpResponseBody<T>.validateNullableDateTime(
        value: OperDateDtoNull,
        roc: RestOperCodeEnum,
        field: Field
    ) = value?.let {
        validateDateTime(value, roc, field)
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validateDate(
        value: OperDateDto,
        roc: RestOperCodeEnum,
        field: Field
    ) = validateLong(value, MIN_DATE_TIME_LONG, MAX_DATE_TIME_LONG, roc, field) {}

    fun <T : ResponseDto> HttpResponseBody<T>.validateNullableDate(
        value: OperDateDtoNull,
        roc: RestOperCodeEnum,
        field: Field
    ) = value?.let {
        validateDate(value, roc, field)
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validateLong(
        value: Long,
        from: Long,
        to: Long,
        roc: RestOperCodeEnum,
        field: Field
    ) =
        validateLong(value, from, to, roc, field) {}


    inline fun <T : ResponseDto> HttpResponseBody<T>.validateLong(
        value: Long,
        roc: RestOperCodeEnum,
        field: Field,
        func: Long2Unit
    ) =
        if (!((value > LONG_ZERO) && (value < Long.MAX_VALUE))) {
            addErrorInfo(
                roc,
                INVALID_ENTITY_ATTR, field, "${field.name}: Invalid long non zero value - [$value]"
            )
        } else {
            func(value)
        }

    inline fun <T : ResponseDto> HttpResponseBody<T>.validateLong(
        value: Long,
        from: Long,
        to: Long,
        roc: RestOperCodeEnum,
        field: Field,
        func: Long2Unit
    ) =
        if (!((value > from) && (value < to))) {
            addErrorInfo(
                roc,
                INVALID_ENTITY_ATTR,
                field,
                "${field.name}: Invalid Long value - [$value] <> [$from , $to]"
            )
        } else {
            func(value)
        }

    inline fun <T : ResponseDto> HttpResponseBody<T>.validateInt(
        value: Int,
        from: Int,
        to: Int,
        roc: RestOperCodeEnum,
        field: Field,
        func: Int2Unit
    ) =
        if (!((value > from) && (value < to))) {
            addErrorInfo(
                roc,
                INVALID_ENTITY_ATTR,
                field,
                "${field.name}: Invalid Long value - [$value] <> [$from , $to]"
            )
        } else {
            func(value)
        }


    //==================================================================================================================
    fun <T : ResponseDto> HttpResponseBody<T>.validateLongRange(
        value1: Long,
        value2: Long,
        roc: RestOperCodeEnum,
        field: Field
    ) = validateLongRange(value1, value2, roc, field) {}

    inline fun <T : ResponseDto> HttpResponseBody<T>.validateLongRange(
        value1: Long,
        value2: Long,
        roc: RestOperCodeEnum,
        field: Field,
        func: NoArg2Unit
    ) = if (value1 >= value2) {
        addErrorInfo(
            roc,
            INVALID_ENTITY_ATTR,
            field,
            "${field.name}: Invalid Long range value - [$value1 , $value2]"
        )
    } else {
        func()
    }

    //collection should not be empty
    inline fun <T : ResponseDto, C> HttpResponseBody<T>.validateNonEmptyCollection(
        collection: Collection<C>,
        roc: RestOperCodeEnum,
        field: Field,
        successFunc: Collection2Unit<C>
    ) {
        if (collection.isEmpty()) {
            addErrorInfo(
                roc,
                INVALID_ENTITY_ATTR,
                field,
                "${field.name}: Collection is empty"
            )
        } else {
            successFunc(collection)
        }
    }

    //====================================================================================
    fun <T : ResponseDto, C> HttpResponseBody<T>.validateNullableEmptyCollection(
        collection: Collection<C>?,
        roc: RestOperCodeEnum,
        field: Field
    ) {
        collection?.let {
            validateNonEmptyCollection(it, roc, field) {}
        }
    }

    fun <T : ResponseDto, C> HttpResponseBody<T>.validateListItem(
        value: C,
        collection: Collection<C>,
        roc: RestOperCodeEnum,
        field: Field
    ) = validateListItem(value, collection, roc, field) { }

    fun <T : ResponseDto, C> HttpResponseBody<T>.validateListItem(
        value: C,
        collection: Collection<C>,
        roc: RestOperCodeEnum,
        field: Field,
        successFunc: GenericArg2Unit<C>
    ) {
        validateNonEmptyCollection(collection, roc, field) {
            if (!collection.contains(value)) {
                addErrorInfo(
                    roc,
                    INVALID_ENTITY_ATTR,
                    field,
                    "${field.name}: invalid field value: '$value', expected value(s): ($collection)"
                )
            } else {
                successFunc(value)
            }
        }
    }

    fun <T : ResponseDto, C> HttpResponseBody<T>.validateEmptyCollection(
        collection: Collection<C>,
        roc: RestOperCodeEnum,
        field: Field
    ) {
        validateEmptyCollection(collection, roc, field) {}
    }

    //collection should be empty
    inline fun <T : ResponseDto, C> HttpResponseBody<T>.validateEmptyCollection(
        collection: Collection<C>,
        roc: RestOperCodeEnum,
        field: Field,
        successFunc: Collection2Unit<C>
    ) {
        if (collection.isNotEmpty()) {
            addErrorInfo(
                roc,
                INVALID_ENTITY_ATTR,
                field,
                "${field.name}: Collection should by empty"
            )
        } else {
            successFunc(collection)
        }
    }

    private fun getPatternString(pattern: Pattern) = pattern.pattern().first15()

}
