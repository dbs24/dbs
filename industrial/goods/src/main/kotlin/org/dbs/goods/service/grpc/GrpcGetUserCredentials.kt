package org.dbs.goods.service.grpc

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.application.core.service.funcs.Patterns.STR4M
import org.dbs.consts.GrpcConsts.ContextKeys.CK_JWT_CLAIMS
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.consts.SecurityConsts.Claims.CL_INTERNAL_SERVICE
import org.dbs.consts.SysConst.NOT_ASSIGNED
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_USER_LOGIN
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.consts.RESP
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.goods.model.user.User
import org.dbs.goods.service.ApplicationServiceGate.ServicesList.userService
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_RESPONSE_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.SSS_USER_LOGIN
import org.dbs.goods.client.UserCredentials as ENT
import org.dbs.goods.client.UserCredentialsRequest as REQ

object GrpcGetUserCredentials {

    suspend fun GoodsGrpcService.getUserCredentialsInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = run {
        validateRemoteAddress(remoteAddress)
        buildGrpcResponse {
            ResponseCoProcessorWrapper(object : ResponseCoProcessor<REQ, ENT.Builder> {
                // main flow
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                override suspend fun execute() = executeIternal {
                    findUser()
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                private val user by lazy { LateInitVal<User>("user") }

                //======================================================================================================
                override fun isValidDto() = request.run dto@{
                    with(rab) {

                        require(CK_JWT_CLAIMS.get()[CL_INTERNAL_SERVICE] ?: NOT_ASSIGNED == CL_INTERNAL_SERVICE)
                        { "illegal call (missing claim '$CL_INTERNAL_SERVICE')" }

                        validateMandatoryField(
                            userLogin,
                            LOGIN_PATTERN.takeUnless { userLogin == ROOT_USER } ?: STR4M,
                            SSS_USER_LOGIN
                        ) {
                            // found user
                            logger.debug("receive user login: '$userLogin'")
                        }
                        noErrors()
                    }
                }

                //------------------------------------------------------------------------------------------------------------------
                suspend fun findUser() {
                    userService.findUserByLogin(request.userLogin)
                        ?.apply { user.init(this) }
                        ?: apply {
                            rab.addErrorInfo(
                                RC_INVALID_RESPONSE_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_USER_LOGIN,
                                findI18nMessage(FLD_UNKNOWN_USER_LOGIN, request.userLogin)
                            )
                        }
                }

                //--------------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(ENT.newBuilder()) {
                    it.setUserLogin(user.value.login)
                        .setUserPassword(user.value.password)
                        .setUserStatus(user.value.status().entityStatusName)
                }

                //------------------------------------------------------------------------------------------------------
                override val jobsMap by lazy { defaultJobsMap() }
                override val rab by lazy { it }
                override val coroutineScope by lazy { defaultCoroutineScope() }

            })
        }
    }
}