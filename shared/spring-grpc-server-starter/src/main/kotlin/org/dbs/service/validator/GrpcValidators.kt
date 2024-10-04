package org.dbs.service.validator

import com.google.protobuf.ByteString
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.api.PersistenceService
import org.dbs.application.core.service.funcs.Patterns.EMAIL_PATTERN
import org.dbs.application.core.service.funcs.Patterns.STR10N
import org.dbs.application.core.service.funcs.Patterns.STR3N
import org.dbs.application.core.service.funcs.Patterns.STR50U
import org.dbs.application.core.service.funcs.StringFuncs.first15
import org.dbs.application.core.service.funcs.StringFuncs.ifNotEmpty
import org.dbs.consts.*
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_SORT_ORDER
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAX_PAGES_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAX_PAGE_SIZE_VALUE
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.TEN_SECONDS
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.entity.core.v2.status.EntityStatusExtension.isAllowedStatusUpdate
import org.dbs.entity.core.v2.status.EntityStatusName
import org.dbs.entity.core.v2.type.EntityTypeExtension.findEntityStatus
import org.dbs.enums.I18NEnum
import org.dbs.enums.I18NEnum.FLD_INVALID_FIELD_VALUE_4_PATTERN
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_COUNTRY
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_REGION
import org.dbs.grpc.ext.GrpcNull.grpcGetOrOne
import org.dbs.grpc.ext.GrpcNull.grpcGetOrTen
import org.dbs.grpc.ext.ResponseAnswerObj.hasErrors
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.protobuf.core.ErrorMessage
import org.dbs.protobuf.core.ResponseAnswer
import org.dbs.protobuf.core.ResponseCode
import org.dbs.protobuf.core.ResponseCode.RC_INTERNAL_ERROR
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.ref.serv.enums.GenderEnum
import org.dbs.ref.serv.service.CountryFuncs.isValidCountryCode
import org.dbs.ref.serv.service.RegionFuncs.isValidRegionCode
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.MonoRAB
import org.dbs.service.RA
import org.dbs.service.RAB
import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.validator.Error
import org.dbs.validator.Error.GENERAL_ERROR
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Error.INVALID_ENTITY_STATUS
import org.dbs.validator.Error.INVALID_FIELD_VALUE
import org.dbs.validator.Error.INVALID_QUERY_PARAM
import org.dbs.validator.Error.QP_INVALID_QUERY_PARAM
import org.dbs.validator.Field
import org.dbs.validator.Field.COUNTRY
import org.dbs.validator.Field.REGION
import org.dbs.validator.Field.SSS_PLAYER_PASSWORD
import org.dbs.validator.Field.SSS_QP_PAGE_NUM
import org.dbs.validator.Field.SSS_QP_PAGE_SIZE
import org.dbs.validator.Field.SSS_SORT_FIELD
import org.dbs.validator.Field.SSS_SORT_ORDER
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import java.util.regex.Pattern
import kotlin.reflect.KClass

object GrpcValidators : Logging {
    fun foundStdEntityServiceMsg(clazz: KClass<*>, entityCode: String) =
        "${clazz.simpleName}: found entity '$entityCode'"
    
    fun RAB.validateFieldNotEmpty(value: String, field: Field) {
        if (value.trim().isEmpty()) {
            addEmptyFieldError(field)
        }
    }

    fun RAB.validateFieldNotEmpty(value: ByteString, field: Field) {
        if (value.isEmpty()) {
            addEmptyFieldError(field)
        }
    }

    fun RAB.validateFieldNotEmpty(value: Collection<*>, field: Field) {
        if (value.isEmpty()) {
            addEmptyFieldError(field)
        }
    }

    private fun RAB.addEmptyFieldError(field: Field) {
        addErrorInfo(
            RC_INVALID_REQUEST_DATA,
            INVALID_QUERY_PARAM,
            field,
            "${field.name} is empty"
        )
    }

    fun RAB.validateMandatoryField(
        paramValue: QueryParamString?,
        pattern: Pattern,
        field: Field,
    ) = validateMandatoryField(paramValue, pattern, field) {}

    inline fun RAB.validateMandatoryField(
        paramValue: QueryParamString?,
        pattern: Pattern,
        field: Field,
        successFunc: String2Unit,
    ): String = validateMandatoryField(paramValue, setOf(pattern), field, successFunc)

    inline fun RAB.validateMandatoryField(
        paramValue: QueryParamString?,
        pattern: Pattern,
        field: Field,
        i18NEnum: I18NEnum,
    ): String = validateMandatoryField(paramValue, setOf(pattern), field, i18NEnum) {}

    inline fun RAB.validateMandatoryField(
        paramValue: QueryParamString?,
        pattern: Pattern,
        field: Field,
        i18NEnum: I18NEnum,
        successFunc: String2Unit,
    ): String = validateMandatoryField(paramValue, setOf(pattern), field, i18NEnum, successFunc)

    inline fun RAB.validateMandatoryField(
        paramValue: QueryParamString?,
        patterns: Set<Pattern>,
        field: Field,
        i18NEnum: I18NEnum,
        successFunc: String2Unit,
    ): String = run {
        paramValue?.let {
            if (!patterns.any { it.matcher(paramValue).matches() }) {
                apply {
                    addErrorInfo(
                        RC_INVALID_REQUEST_DATA,
                        INVALID_QUERY_PARAM, field,
                        findI18nMessage(i18NEnum)
                    )
                }
            } else {
                successFunc(paramValue)
            }
            paramValue.trim()
        } ?: run {
            addErrorInfo(
                RC_INVALID_REQUEST_DATA,
                INVALID_QUERY_PARAM,
                field,
                "${field.name}: request param is null or empty"
            )
            EMPTY_STRING
        }
    }

    fun RAB.validatePasswordField(password: QueryParamString) = true.takeIf { password.length in 3..50 }
        ?: addErrorInfo(
            RC_INVALID_REQUEST_DATA,
            INVALID_ENTITY_ATTR,
            SSS_PLAYER_PASSWORD,
            "invalid login or password"
        )

//    fun RAB.getClaimWithCheck(claims: Claims, claimName: String, jwt: String = UNKNOWN): String? = run {
//        claims[claimName]?.toString() ?: run {
//            addErrorInfo(
//                RC_INVALID_JWT,
//                FIELD_NOT_FOUND,
//                JWT_TOKEN_CLAIM,
//                findI18nMessage(JWT_CLAIM_IS_MISSING, claimName, jwt.last15())
//            )
//            STRING_NULL
//        }
//    }

    inline fun RAB.validateMandatoryField(
        paramValue: QueryParamString?,
        patterns: Set<Pattern>,
        field: Field,
    ): String = validateMandatoryField(paramValue, patterns, field) {}

    inline fun RAB.validateMandatoryField(
        paramValue: QueryParamString?,
        patterns: Set<Pattern>,
        field: Field,
        successFunc: String2Unit,
    ): String = run {
        paramValue?.let {
            if (!patterns.any { it.matcher(paramValue).matches() }) {
                addErrorInfo(
                    RC_INVALID_REQUEST_DATA,
                    INVALID_FIELD_VALUE,
                    field,
                    "${field.name}: Invalid param value '${paramValue.first15()}' "
                )
            } else {
                successFunc(paramValue)
            }
            paramValue.trim()
        } ?: run {
            addErrorInfo(
                RC_INVALID_REQUEST_DATA,
                INVALID_QUERY_PARAM,
                field,
                "${field.name}: request param is null or empty"
            )
            EMPTY_STRING
        }
    }

    inline fun RAB.validateOptionalField(
        value: QueryParamString?,
        patterns: Set<Pattern>,
        field: Field,
    ) = value?.let {
        validateOptionalField(value, patterns, field) {}
    }

    inline fun RAB.validateOptionalField(
        value: QueryParamString?,
        patterns: Set<Pattern>,
        field: Field,
        successFunc: String2Unit
    ) = value?.let {
        validateMandatoryField(value, patterns, field, successFunc)
    }

    fun RAB.validateOptionalField(
        value: String?,
        pattern: Pattern,
        field: Field,
    ) = value.ifNotEmpty { validateOptionalField(value, pattern, field) {} }

    fun RAB.validateOptionalField(
        value: String?,
        pattern: Pattern,
        field: Field,
        successFunc: String2Unit,
    ) = value.ifNotEmpty { validateField(checkNotNull(value), pattern, field, successFunc) }

    fun RAB.validateGender(
        gender: String,
        field: Field,
    ) = validateGender(gender, field) {}

    fun RAB.validateGender(
        gender: String,
        field: Field,
        func: String2Unit,
    ) = run {
        if (GenderEnum.isExistsEnum(gender.trim().uppercase()))
            func(gender)
        else run {
            addErrorInfo(
                RC_INVALID_REQUEST_DATA,
                INVALID_ENTITY_ATTR,
                field,
                "unknown gender value: '$gender'"
            )
        }
    }

    private inline fun RAB.validateField(
        value: String,
        pattern: Pattern,
        field: Field,
        successFunc: String2Unit,
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
                    RC_INVALID_REQUEST_DATA,
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

    fun RAB.addErrorInfo(responseCode: ResponseCode, error: Error, field: Field, errorMsg: String): RAB =
        errorMessagesList
            .filter { it.code == error.getCode() }
            .map { this }
            .firstOrNull()
            ?: addErrorMessages(
                ErrorMessage.newBuilder()
                    .setCode(error.getCode())
                    .setMessage("${field.name}: ${error.name}: '$errorMsg'")
                    .build().also {
                        logger.warn { "register custom error: [$it]" }
                        if (errorMessage == EMPTY_STRING) {
                            errorMessage = it.message
                            setResponseCode(responseCode)
                        }
                    })

    fun RAB.addErrorInfo(errorMsg: String): RAB =
        addErrorMessages(
            ErrorMessage.newBuilder()
                .setCode(GENERAL_ERROR.getCode())
                .setMessage(errorMsg)
                .build().also {
                    logger.warn { "register custom error: [$it]" }
                    if (errorMessage == EMPTY_STRING) {
                        errorMessage = errorMsg
                        setResponseCode(RC_INVALID_REQUEST_DATA)
                    }
                })

    fun Throwable.buildResponseError() : ResponseAnswer = this.toString().let {
        ResponseAnswer.newBuilder()
            .setResponseCode(RC_INTERNAL_ERROR)
            //.setResponseEntity(responseEntity)
            .setErrorMessage(it)
            .addErrorInfo(it)
            .build()
    }

    fun RAB.importErrorsIfAny(answer: RA): Boolean = run {
        if (answer.hasErrors()) {
            logger.warn { "assign errors: ${answer.errorMessagesList}" }
            setErrorMessage(answer.errorMessage)
            answer.errorMessagesList.forEach { addErrorMessages(it) }
            setResponseCode(answer.responseCode)
        }
        hasErrors()
    }

    val persistenceService by lazy { findService(PersistenceService::class) }

    inline fun RAB.inTransaction(
        crossinline transactionBatch: NoArg2Mono<RAB>,
    ): MonoRAB = run {
        if (noErrors()) {
            val trBatch = transactionBatch().cache(TEN_SECONDS)
            trBatch
                .filter { it.noErrors() }
                .flatMap {
                    persistenceService
                        .transactionalOperator
                        .execute { trBatch }
                        .single()
                }
        } else
            toMono()
    }

    inline fun RAB.validatePageSize(
        paramValue: QueryParamString?,
        successFunc: Int2Unit,
    ) = validatePageSize(paramValue, MAX_PAGE_SIZE_VALUE, successFunc)

    inline fun RAB.validatePageSize(
        paramValue: QueryParamString?,
        maxPageSize: Int,
        successFunc: Int2Unit,
    ) = paramValue?.grpcGetOrTen().let { pageSize ->
        validateMandatoryField(
            pageSize,
            STR3N,
            SSS_QP_PAGE_SIZE
        ) {
            validateMinMax(
                pageSize!!.toInt(),
                1,
                maxPageSize,
                QP_INVALID_QUERY_PARAM,
                SSS_QP_PAGE_SIZE,
                successFunc
            )
        }
    }

    fun RAB.breakWhenErrors(): Mono<RAB> = run { if (noErrors()) { toMono() } else empty() }

    inline fun RAB.validatePageNum(
        paramValue: QueryParamString?,
        successFunc: Int2Unit,
    ) = validatePageNum(paramValue, MAX_PAGES_VALUE, successFunc)

    inline fun RAB.validatePageNum(
        paramValue: QueryParamString?,
        maxPages: Int,
        successFunc: Int2Unit,
    ) = paramValue?.grpcGetOrOne().let { pageNum ->
        validateMandatoryField(
            pageNum,
            STR10N,
            SSS_QP_PAGE_NUM
        ) {
            validateMinMax(
                pageNum!!.toInt(),
                1,
                maxPages,
                QP_INVALID_QUERY_PARAM,
                SSS_QP_PAGE_NUM,
                successFunc
            )
        }
    }

    fun RAB.validateSortField(
        sortField: String?,
        defaultValue: String,
        setSortFieldFun: String2Unit
    ) {
        sortField?.let {
            validateMandatoryField(it, STR50U, SSS_SORT_FIELD) {
                setSortFieldFun(it.trim())
            }
        } ?: setSortFieldFun(defaultValue)
    }

    fun RAB.validateSortOrder(
        sortOrder: String?,
        defaultValue: String,
        setSortOrderFun: String2Unit
    ) {
        sortOrder?.let {
            val order = it.trim().uppercase()
            if (order == "ASC" || order == "DESC") {
                setSortOrderFun(order)
            } else {
                addErrorInfo(
                    RC_INVALID_REQUEST_DATA,
                    INVALID_QUERY_PARAM,
                    SSS_SORT_ORDER,
                    "$QP_SORT_ORDER: Invalid query param value '$it'. Allowed values: [ASC, DESC]"
                )
            }
        } ?: setSortOrderFun(defaultValue)
    }

    inline fun RAB.validateDateNotBefore(
        value: LocalDateTime,
        border: LocalDateTime,
        field: Field
    ) {
        if (value.isBefore(border)) {
            addErrorInfo(
                RC_INVALID_REQUEST_DATA,
                INVALID_FIELD_VALUE,
                field,
                "Illegal date param value: '$value' " +
                        "(must not be before [$border])"
            )
        }
    }

    inline fun RAB.validateMinMax(
        value: Int,
        minValue: Int,
        maxValue: Int,
        error: Error = INVALID_FIELD_VALUE,
        field: Field,
    ) = validateMinMax(value, minValue, maxValue, error, field) {}

    inline fun RAB.validateMinMax(
        value: Int,
        minValue: Int,
        maxValue: Int,
        error: Error = INVALID_FIELD_VALUE,
        field: Field,
        successFunc: Int2Unit,
    ) = value.run {
        val validValue = (value >= minValue) && (value <= maxValue)
        if (!validValue) {
            addErrorInfo(
                RC_INVALID_REQUEST_DATA,
                error,
                field,
                "Illegal param value: '$value' " +
                        "(not in range [$minValue,$maxValue])"
            )
        }
        if (validValue) {
            successFunc(value)
        }
    }

    inline fun RAB.validateParamPatternIfPresent(
        queryParamValue: QueryParamString?,
        pattern: Pattern,
        field: Field,
        successFunc: String2Unit,
    ) = validateParamPatternIfPresent(queryParamValue, kotlin.collections.setOf(pattern), field, successFunc)

    inline fun RAB.validateParamPatternIfPresent(
        queryParamValue: QueryParamString?,
        patterns: Set<Pattern>,
        field: Field,
        successFunc: String2Unit,
    ) = queryParamValue.apply {
        queryParamValue?.apply {
            validateMandatoryField(this, patterns, field)
            { successFunc(this) }
        }
    }

    fun RAB.validateEntityStatus(
        queryParamValue: QueryParamString,
        responseCodeEnum: ResponseCode,
        items: List<String>,
        field: Field,
        successFunc: String2Unit,
    ) {
        val status = queryParamValue.trim().uppercase()
        if (!items.contains(status)) {
            addErrorInfo(
                responseCodeEnum,
                INVALID_ENTITY_STATUS,
                field,
                "${field.name}: Unknown or illegal entity status ($status)"
            )
        } else successFunc(status)
    }

    fun RAB.validateEntityStatusIfPresent(
        queryParamValue: QueryParamString?,
        responseCodeEnum: ResponseCode,
        items: List<String>,
        field: Field,
        successFunc: String2Unit,
    ) = queryParamValue?.apply {
        val status = queryParamValue.trim().uppercase()
        if (!items.contains(status)) {
            addErrorInfo(
                responseCodeEnum,
                INVALID_ENTITY_STATUS,
                field,
                "${field.name}: Unknown or illegal entity status ($status)"
            )
        } else {
            successFunc(status)
        }
    }

//    fun <AE : AbstractEntity> RAB.validateClosedStatuses(
//        entity: AE,
//        field: Field,
//        vararg entityStatusesClosedEnum: EntityStatusEnum,
//    ) {
//        entityStatusesClosedEnum.forEach {
//            validateClosedStatus(entity, it, field)
//        }
//    }
//
//    private fun <AE : AbstractEntity> RAB.validateClosedStatus(
//        entity: AE,
//        entityStatusClosedEnum: EntityStatusEnum,
//        field: Field,
//    ) {
//        if (!entity.justCreated && entity.status() == entityStatusClosedEnum) {
//            addErrorInfo(
//                RC_INVALID_REQUEST_DATA,
//                INVALID_ENTITY_STATUS,
//                field,
//                "${entity.javaClass.simpleName} status is ${entityStatusClosedEnum.name} " +
//                        "(${entity.getCoreEntity().entityId}, ${entity.status()})"
//            )
//        }
//    }
    
    fun RAB.validateEmail(
        value: String,
        field: Field,
    ) = validateMandatoryField(value, EMAIL_PATTERN, field) {}

    fun RAB.validateOptionalEmail(
        value: String?,
        field: Field,
    ) = value?.let { validateEmail(it, field) }

    private fun getPatternString(pattern: Pattern) = pattern.pattern().first15()

    fun RAB.validateCountry(value: String, field: Field = COUNTRY) = validateCountry(value, field) {}

    inline fun RAB.validateCountry(value: String, field: Field = COUNTRY, func: String2Unit) =
        if (!value.isValidCountryCode()) {
            addErrorInfo(
                RC_INVALID_REQUEST_DATA, INVALID_ENTITY_ATTR, field, "${field.name}: " +
                        "${findI18nMessage(FLD_UNKNOWN_COUNTRY)} ($value)"
            )
            Unit
        } else {
            func(value)
        }

    fun RAB.validateRegion(
        value: String,
        field: Field = REGION,
    ) =
        validateRegion(value, field) {}

    inline fun RAB.validateRegion(
        value: String,
        field: Field = REGION,
        func: String2Unit,
    ) = if (!value.isValidRegionCode()) {
            addErrorInfo(
                RC_INVALID_REQUEST_DATA,
                INVALID_ENTITY_ATTR,
                field,
                "${field.name}: ${findI18nMessage(FLD_UNKNOWN_REGION)} ($value)"
            )
            Unit
        } else {
            func(value)
        }

    fun <C> RAB.validateListItem(
        value: C,
        collection: Collection<C>,
        field: Field,
        successFunc: GenericArg2Unit<C>,
    ) {
        validateNonEmptyCollection(collection, field) {
            if (!collection.contains(value)) {
                addErrorInfo(
                    RC_INVALID_REQUEST_DATA,
                    INVALID_ENTITY_ATTR,
                    field,
                    "${field.name}: invalid field value: '$value', expected value(s): ($collection)"
                )
            } else {
                successFunc(value)
            }
        }
    }

    inline fun <C> RAB.validateNonEmptyCollection(
        collection: Collection<C>,
        field: Field,
        successFunc: Collection2Unit<C>,
    ) {
        if (collection.isEmpty()) {
            addErrorInfo(
                RC_INVALID_REQUEST_DATA,
                INVALID_ENTITY_ATTR,
                field,
                "${field.name}: Collection is empty"
            )
        } else {
            successFunc(collection)
        }
    }

    inline fun <ET : EntityTypeEnum> RAB.findEntityStatus(
        entityType: ET,
        entityStatusName: EntityStatusName,
        field: Field,
        onSuccessAction: (EntityStatusEnum) -> Unit,
    ) = entityType.findEntityStatus(entityStatusName)?.apply {
        onSuccessAction(this)
    } ?: apply {
        addErrorInfo(
            RC_INVALID_REQUEST_DATA,
            INVALID_ENTITY_STATUS,
            field,
            "${field.name}: $entityType (unknown entityStatus [$entityStatusName]"
        )
    }


    fun <AE : EntityCore> RAB.validateEntityUpdateStatus(
        entity: AE,
        newEntityStatus: EntityStatusEnum,
        field: Field
    ) = entity.status().isAllowedStatusUpdate(newEntityStatus).apply {
        if (this.isNotEmpty())
            addErrorInfo(
                RC_INVALID_REQUEST_DATA,
                INVALID_ENTITY_STATUS,
                field,
                "${field.name}: $this (entityId=${entity.entityId})"
            )
    }
}
