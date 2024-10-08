package org.dbs.goods.service.grpc

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_USER_LOGIN
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.goods.model.user.User
import org.dbs.goods.service.ApplicationServiceGate.ServicesList.userService
import org.dbs.goods.UserCore.UserActionEnum.EA_UPDATE_USER_STATUS
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.v2.EntityCoreVal.Companion.executeAction
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.findEntityStatus
import org.dbs.service.validator.GrpcValidators.validateEntityUpdateStatus
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.*
import org.dbs.goods.client.CreatedUserStatus as ENT
import org.dbs.goods.client.UpdateUserStatusRequest as REQ
import org.dbs.protobuf.core.MainResponse as RESP

object GrpcUpdateUserStatus {

    suspend fun GoodsGrpcService.updateUserStatusInternal(
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
                val newStatus4update by lazy { LateInitVal<EntityStatusEnum>() }

                override fun isValidDto() = request.run {
                    with(rab) {
                        validateMandatoryField(
                            modifiedLogin,
                            LOGIN_PATTERN,
                            SSS_USER_LOGIN
                        )
                        noErrors()
                    }
                }

                //--------------------------------------------------------------------------------------------------
                private fun validateNewUserStatus() {
                    rab.findEntityStatus(
                        user.value.entityType,
                        request.status,
                        SSS_USER_STATUS
                    ) { newStatus4update.init(it) }

                    if (newStatus4update.isInitialized()) {
                        rab.validateEntityUpdateStatus(
                            user.value,
                            newStatus4update.value,
                            SSS_USER_STATUS
                        )
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                suspend fun findModifiedUser() {
                    with(request) {

                        userService.findUserByLogin(modifiedLogin)
                            ?.apply {
                                user.init(this)
                                validateNewUserStatus()
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
                        setUserNewStatus(
                            user.value,
                            newStatus4update.value
                        )
                        executeAction(user.value, EA_UPDATE_USER_STATUS, remoteAddress, request.toString())
                    }

                //--------------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(ENT.newBuilder()) {
                    it.setModifiedLogin(user.value.login)
                        .setNewStatus(newStatus4update.value.entityStatusName)
                }

                //------------------------------------------------------------------------------------------------------
                override val jobsMap by lazy { defaultJobsMap() }
                override val rab by lazy { it }
                override val coroutineScope by lazy { defaultCoroutineScope() }

            })
        }
    }
}
