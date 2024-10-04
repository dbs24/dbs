package org.dbs.mgmt.service.grpc

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.application.core.service.funcs.Patterns.PASSWORD_PATTERN
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_USER_LOGIN
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.consts.RESP
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.mgmt.model.user.User
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.userService
import org.dbs.customer.UserCore.UserActionEnum.EA_UPDATE_USER_PASSWORD
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.v2.EntityCoreVal.Companion.executeAction
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.*
import org.dbs.mgmt.client.CreatedUserPassword as ENT
import org.dbs.mgmt.client.UpdateUserPasswordRequest as REQ

object GrpcUpdateUserPassword {

    suspend fun MgmtGrpcService.updateUserPasswordInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = run {
        validateRemoteAddress(remoteAddress)
        buildGrpcResponse {
            ResponseCoProcessorWrapper(object : ResponseCoProcessor<REQ, ENT.Builder> {
                // main flow
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                override suspend fun execute() = executeIternal {
                    findModifiedUser()
                    saveEntity()
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////

                private val user by lazy { LateInitVal<User>("user") }

                override fun isValidDto() = request.run {
                    with(rab) {
                        validateMandatoryField(
                            modifiedLogin,
                            LOGIN_PATTERN,
                            SSS_USER_LOGIN
                        )
                        validateMandatoryField(
                            newPassword,
                            PASSWORD_PATTERN,
                            SSS_USER_PASSWORD
                        )
                        noErrors()
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                suspend fun findModifiedUser() {
                    with(request) {
                        userService.findUserByLogin(modifiedLogin)
                            ?.apply {
                                user.init(this)
                            }

                        if (user.isNotInitialized()) let {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_USER_OLD_LOGIN,
                                findI18nMessage(FLD_UNKNOWN_USER_LOGIN, modifiedLogin)
                            )
                        }
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                suspend fun saveEntity() = userService.takeIf { user.isInitialized() }
                    ?.apply {
                        setUserNewPassword(
                            user.value,
                            request.newPassword
                        )
                        executeAction(user.value, EA_UPDATE_USER_PASSWORD, remoteAddress, request.toString())
                    }

                //--------------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(ENT.newBuilder()) {
                    it.setModifiedLogin(user.value.login)
                }

                //------------------------------------------------------------------------------------------------------
                override val jobsMap by lazy { defaultJobsMap() }
                override val rab by lazy { it }
                override val coroutineScope by lazy { defaultCoroutineScope() }

            })
        }
    }
}
