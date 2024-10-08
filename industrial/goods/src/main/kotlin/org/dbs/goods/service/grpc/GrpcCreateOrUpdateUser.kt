package org.dbs.goods.service.grpc

import org.dbs.api.CommonJobs.JK_SAVE
import org.dbs.api.JobKey
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.application.core.service.funcs.Patterns.PASSWORD_PATTERN
import org.dbs.application.core.service.funcs.Patterns.USER_FIRST_NAME_PATTERN
import org.dbs.application.core.service.funcs.Patterns.USER_LAST_NAME_PATTERN
import org.dbs.application.core.service.funcs.StringFuncs.isNull
import org.dbs.consts.Email
import org.dbs.consts.EntityCode
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.enums.I18NEnum.*
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.consts.RESP
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.goods.mapper.UserMappers.updateUser
import org.dbs.goods.model.user.User
import org.dbs.goods.service.ApplicationServiceGate.ServicesList.userService
import org.dbs.goods.service.grpc.GrpcCreateOrUpdateUser.JobKeyImp.JK_FIND_OR_CREATE_USER
import org.dbs.goods.UserCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.v2.EntityCoreVal.Companion.executeAction
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.service.validator.GrpcValidators.validateOptionalEmail
import org.dbs.service.validator.GrpcValidators.validateOptionalField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.*
import org.dbs.goods.client.CreateOrUpdateUserRequest as REQ
import org.dbs.goods.client.CreatedUserDto as ENT

object GrpcCreateOrUpdateUser {

    private enum class JobKeyImp : JobKey {
        JK_FIND_OR_CREATE_USER,
    }

    suspend fun GoodsGrpcService.createOrUpdateUserInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = run {
        validateRemoteAddress(remoteAddress)
        buildGrpcResponse {
            ResponseCoProcessorWrapper(object : ResponseCoProcessor<REQ, ENT.Builder> {
                // main flow
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                override suspend fun execute() = executeIternal {
                    validateNewLogin()
                    findOrCreateUser()
                    saveEntity()
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                private val user by lazy { LateInitVal<User>("user") }
                private val user4Update by lazy { LateInitVal<User>("user4update") }
                private val oldUserLogin by lazy { request.oldLogin.grpcGetOrNull() }

                //======================================================================================================
                override fun isValidDto() = request.run {
                    with(rab) {
                        validateMandatoryField(login, LOGIN_PATTERN, SSS_USER_LOGIN)
                        validateOptionalField(oldLogin, LOGIN_PATTERN, SSS_USER_LOGIN)
                        validateOptionalField(firstName, USER_FIRST_NAME_PATTERN, SSS_USER_FIRST_NAME)
                        validateOptionalField(middleName, USER_LAST_NAME_PATTERN, SSS_USER_LAST_NAME)
                        validateOptionalField(lastName, USER_LAST_NAME_PATTERN, SSS_USER_MIDDLE_NAME)
                        validateOptionalEmail(email, SSS_USER_EMAIL)
                        // is update user
                        oldUserLogin?.let {
                            validateMandatoryField(it, LOGIN_PATTERN, SSS_USER_OLD_LOGIN)
                        } ?: password?.let { userPwd ->
                            validateMandatoryField(userPwd, PASSWORD_PATTERN, SSS_USER_PASSWORD)
                        } ?: addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            SSS_USER_PASSWORD,
                            findI18nMessage(FLD_INVALID_USER_PASSWORD)
                        )
                        noErrors()
                    }
                }

                //------------------------------------------------------------------------------------------------------
                suspend fun validateNewLogin(newLogin: EntityCode) =
                    userService.findUserByLogin(newLogin)
                        ?.apply {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_USER_LOGIN,
                                "${findI18nMessage(EXIST_USER_LOGIN)} '${newLogin}'"
                            )
                        }

                //------------------------------------------------------------------------------------------------------
                suspend fun validateNewEmail(newEmail: Email) =
                    userService.findUserByEmail(newEmail)
                        ?.apply {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_USER_EMAIL,
                                "${findI18nMessage(EXIST_USER_EMAIL)} '${newEmail}'"
                            )
                        }

                //------------------------------------------------------------------------------------------------------
                private fun updateFromDto(user: User): User =
                    userService.updateUser(user, request)
                        .run { user4Update.init(this) }

                //------------------------------------------------------------------------------------------------------
                private suspend fun saveUser(user: User): User =
                    executeAction(user, EA_CREATE_OR_UPDATE_USER, remoteAddress, request.toString())

                //------------------------------------------------------------------------------------------------------
                suspend fun validateNewLogin() =
                    request.apply {
                        val checkNewLogin = oldUserLogin?.let { it != login } ?: true
                        val checkNewEmail = oldUserLogin.isNull() || oldEmail?.let { it != email } ?: true
                        if (checkNewLogin) validateNewLogin(login)
                        if (checkNewEmail) validateNewEmail(email)
                    }

                //------------------------------------------------------------------------------------------------------
                suspend fun findOrCreateUser() = launchJob(JK_FIND_OR_CREATE_USER) {
                    userService.findUserByLogin(oldUserLogin ?: request.login)
                        ?.apply { user.init(this) }
                        ?: oldUserLogin?.let {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_USER_OLD_LOGIN,
                                findI18nMessage(FLD_UNKNOWN_USER_LOGIN, it)
                            )
                        } ?: userService.createNewUser(request.login).apply { user.init(this) }
                }

                //------------------------------------------------------------------------------------------------------
                suspend fun saveEntity() = launchJob(JK_SAVE, JK_FIND_OR_CREATE_USER) {
                    userService.saveHistory(user.value)
                        .also { saveUser(updateFromDto(user.value)) }
                }

                //------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(JK_SAVE, ENT.newBuilder()) {
                    it.setUserLogin(user4Update.value.login)
                        .setEmail(user4Update.value.email)
                        .setStatus(user4Update.value.status().entityStatusName)
                }

                //------------------------------------------------------------------------------------------------------
                override val jobsMap by lazy { defaultJobsMap() }
                override val rab by lazy { it }
                override val coroutineScope by lazy { defaultCoroutineScope() }

            })
        }
    }
}
