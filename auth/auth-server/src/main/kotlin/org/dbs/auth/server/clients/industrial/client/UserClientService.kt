package org.dbs.auth.server.clients.industrial.client

import org.dbs.goods.service.GrpcGoodsClientService
import org.dbs.application.core.service.funcs.IntFuncs.errorS
import org.dbs.consts.EntityConsts.EntityStatuses.ACTUAL
import org.dbs.consts.Login
import org.dbs.enums.I18NEnum.FLD_INVALID_PLAYER_STATUS
import org.dbs.grpc.ext.ResponseAnswerObj.unpackResponseEntity
import org.dbs.goods.client.UserCredentials
import org.dbs.protobuf.core.MainResponse
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_RESPONSE_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.importErrorsIfAny
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.validator.Error.PLAYER_INVALID_STATUS
import org.dbs.validator.Field.SSS_PLAYER_STATUS
import org.springframework.stereotype.Service

@Service
class UserClientService(private val grpcMgmtClientService: GrpcGoodsClientService) : AbstractApplicationService() {

    fun getAndValidateUserCredentials(
        userLogin: Login,
        answerBuilder: RAB,
    ): MainResponse = grpcMgmtClientService.getUserCredentials(userLogin).also {

        answerBuilder.apply {
            if (importErrorsIfAny(it.responseAnswer)) {
                logger.warn { "$userLogin: ${errorMessagesCount.errorS()}" }
            } else {
                // validate status
                logger.warn { "$userLogin: ${errorMessagesCount.errorS()}" }

                with(it.responseAnswer.unpackResponseEntity<UserCredentials>()) {

                    require(userLogin == this.userLogin) { "invalid userLogin - [$userLogin<>${this.userLogin}]" }

                    // allowed user statuses
                    if (userStatus != ACTUAL) {
                        addErrorInfo(
                            RC_INVALID_RESPONSE_DATA,
                            PLAYER_INVALID_STATUS,
                            SSS_PLAYER_STATUS,
                            findI18nMessage(FLD_INVALID_PLAYER_STATUS, userStatus),
                        )
                    }
                }
            }
        }
    }
}
