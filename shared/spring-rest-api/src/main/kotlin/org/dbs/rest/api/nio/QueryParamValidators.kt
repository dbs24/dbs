package org.dbs.rest.api.nio

import org.dbs.validator.Error
import org.dbs.validator.Error.INVALID_JWT
import org.dbs.validator.Error.INVALID_QUERY_PARAM
import org.dbs.validator.Error.QP_INVALID_QUERY_PARAM
import org.dbs.validator.Field
import org.dbs.validator.Field.SSS_QP_PAGE_NUM
import org.dbs.validator.Field.SSS_QP_PAGE_SIZE
import org.dbs.validator.Field.SSS_SORT_FIELD
import org.dbs.validator.Field.SSS_SORT_ORDER
import org.dbs.application.core.service.funcs.Patterns.JWT_PATTERN
import org.dbs.application.core.service.funcs.Patterns.STR10N
import org.dbs.application.core.service.funcs.Patterns.STR3N
import org.dbs.application.core.service.funcs.Patterns.STR50U
import org.dbs.consts.Int2Unit
import org.dbs.consts.Jwt
import org.dbs.consts.Long2Unit
import org.dbs.consts.QueryParamName
import org.dbs.consts.QueryParamString
import org.dbs.consts.RestHttpConsts.RestQueryParams.Pagination.QP_PAGE_NUM
import org.dbs.consts.RestHttpConsts.RestQueryParams.Pagination.QP_PAGE_SIZE
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_SORT_FIELD
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_SORT_ORDER
import org.dbs.consts.String2Unit
import org.dbs.consts.SysConst
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.consts.SysConst.STRING_ONE
import org.dbs.consts.SysConst.STRING_TEN
import org.dbs.rest.api.enums.RestOperCodeEnum
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_QUERY_PARAM_ILLEGAL_MIN_LENGTH
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_QUERY_PARAM_INVALID_VALUE
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_QUERY_PARAM_NOT_FOUND
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.queryParamOrNull
import java.util.regex.Pattern

object QueryParamValidators {
    fun <T : ResponseDto> HttpResponseBody<T>.registryMandatoryQueryParamNotFoundException(
        path: String,
        queryParamName: String,
        error: Error,
        field: Field
    ) {
        addErrorInfo(
            OC_QUERY_PARAM_NOT_FOUND,
            error,
            field,
            "${path.uppercase()}: mandatory query param not found or empty: '$queryParamName'"
        )
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validateMinLength(
        path: String,
        queryParamName: String,
        queryParamValue: String,
        minimalParamLength: Int,
        error: Error,
        field: Field,
        successFunc: String2Unit
    ) = queryParamValue.run {
        val invalidLength = (this.trim().length < minimalParamLength)
        if (invalidLength) {
            addErrorInfo(
                OC_QUERY_PARAM_ILLEGAL_MIN_LENGTH,
                error,
                field,
                "${path.uppercase()}: illegal query min width '$queryParamName', " +
                        "(${queryParamValue.trim().length < minimalParamLength}<$minimalParamLength)"
            )
        } else {
            successFunc(queryParamValue)
        }
    }

    inline fun <T : ResponseDto> HttpResponseBody<T>.validateMinMax(
        queryParamName: String,
        queryParamValue: Int,
        queryParamMinValue: Int,
        queryParamMaxValue: Int,
        error: Error,
        field: Field,
        successFunc: Int2Unit
    ) = queryParamValue.run {
        val validValue = (queryParamValue >= queryParamMinValue) && (queryParamValue <= queryParamMaxValue)
        if (!validValue) {
            addErrorInfo(
                OC_QUERY_PARAM_INVALID_VALUE,
                error,
                field,
                "illegal query value: $queryParamName = '$queryParamValue' " +
                        "(not in range [$queryParamMinValue,$queryParamMaxValue])"
            )
        }
        if (validValue) {
            successFunc(queryParamValue)
        }
    }

    inline fun <T : ResponseDto> HttpResponseBody<T>.validateParamPatternIfPresent(
        serverRequest: ServerRequest,
        queryParamName: QueryParamName,
        pattern: Pattern,
        field: Field,
        successFunc: String2Unit
    ) = serverRequest.apply {
        this.queryParamOrNull(queryParamName)?.apply {
            validateParamPattern(queryParamName, this, pattern, field)
            { successFunc(this) }
        }
    }

    inline fun <T : ResponseDto> HttpResponseBody<T>.validateMandatoryParamPattern(
        serverRequest: ServerRequest,
        queryParamName: QueryParamName,
        pattern: Pattern,
        field: Field,
        successFunc: String2Unit
    ) = serverRequest.apply {
        this.queryParam(queryParamName).orElse(STRING_NULL)?.run {
            this.trim().ifEmpty { STRING_NULL }
        }?.apply {
            validateParamPattern(queryParamName, this, pattern, field)
            { successFunc(this) }
        } ?: run {
            registryMandatoryQueryParamNotFoundException(this.path(), queryParamName, QP_INVALID_QUERY_PARAM, field)
        }
    }

    inline fun <T : ResponseDto> HttpResponseBody<T>.validateJwt(
        value: Jwt,
        roc: RestOperCodeEnum,
        field: Field,
        successFunc: String2Unit
    ) = run {
        val pattern = JWT_PATTERN
        if (!pattern.matcher(value).matches()) {
            apply {
                addErrorInfo(
                    roc,
                    INVALID_JWT, field,
                    "${field.name}: Invalid jwt '$value' "
                )
            }
        } else {
            successFunc(value)
        }
    }


    inline fun <T : ResponseDto> HttpResponseBody<T>.validateLong(
        value: Long,
        roc: RestOperCodeEnum,
        field: Field,
        func: Long2Unit
    ) = run {
        if (!((value > SysConst.LONG_ZERO) && (value < Long.MAX_VALUE))) {
            apply {
                addErrorInfo(
                    roc,
                    INVALID_QUERY_PARAM, field,
                    "${field.name}: Invalid query param value '$value' "
                )
            }
        } else {
            func(value)
        }
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validateParamPattern(
        queryParamName: QueryParamName,
        queryParamValue: QueryParamString,
        pattern: Pattern,
        field: Field
    ) = validateParamPattern(queryParamName, queryParamValue, pattern, field) {}

    inline fun <T : ResponseDto> HttpResponseBody<T>.validateParamPattern(
        queryParamName: QueryParamName,
        queryParamValue: QueryParamString,
        pattern: Pattern,
        field: Field,
        successFunc: String2Unit
    ) = run {
        if (!pattern.matcher(queryParamValue).matches()) {
            apply {
                addErrorInfo(
                    OC_QUERY_PARAM_INVALID_VALUE,
                    INVALID_QUERY_PARAM, field,
                    "$queryParamName: Invalid query param value '$queryParamValue' "
                )
            }
        } else {
            successFunc(queryParamValue)
        }
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validatePageSize(
        serverRequest: ServerRequest,
        maxPageSize: Int,
        successFunc: Int2Unit
    ) = serverRequest.queryParam(QP_PAGE_SIZE).orElse(STRING_TEN).let { pageSize ->
        validateParamPattern(QP_PAGE_SIZE, pageSize, STR3N, SSS_QP_PAGE_SIZE) {
            validateMinMax(
                QP_PAGE_SIZE,
                pageSize.toInt(),
                1,
                maxPageSize,
                QP_INVALID_QUERY_PARAM,
                SSS_QP_PAGE_SIZE,
                successFunc
            )
        }
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validatePageNum(
        serverRequest: ServerRequest, maxPages: Int, successFunc: Int2Unit
    ) = serverRequest.queryParam(QP_PAGE_NUM).orElse(STRING_ONE).let { pageNum ->
        validateParamPattern(QP_PAGE_NUM, pageNum, STR10N, SSS_QP_PAGE_NUM) {
            validateMinMax(
                QP_PAGE_NUM,
                pageNum.toInt(),
                1,
                maxPages,
                QP_INVALID_QUERY_PARAM,
                SSS_QP_PAGE_NUM,
                successFunc
            )
        }
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validateSortField(
        serverRequest: ServerRequest, setSortFieldFun: String2Unit
    ) = serverRequest.queryParam(QP_SORT_FIELD).ifPresent {
        validateParamPattern(QP_SORT_FIELD, it, STR50U, SSS_SORT_FIELD) {
            setSortFieldFun(it.trim())
        }
    }

    fun <T : ResponseDto> HttpResponseBody<T>.validateSortOrder(
        serverRequest: ServerRequest, setSortOrderFun: String2Unit
    ) = serverRequest.queryParam(QP_SORT_ORDER).ifPresent {
        val order = it.trim().uppercase()
        if (order == "ASC" || order == "DESC") {
            setSortOrderFun(order)
        } else {
            addErrorInfo(
                OC_QUERY_PARAM_INVALID_VALUE,
                INVALID_QUERY_PARAM,
                SSS_SORT_ORDER,
                "$QP_SORT_ORDER: Invalid query param value '$it'. Allowed values: [ASC, DESC]"
            )
        }
    }
}
