package org.dbs.analyst.service.grpc

import org.dbs.analyst.dao.PlayerDao
import org.dbs.analyst.model.player.Player
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
import org.dbs.ext.GrpcFuncs.fmFinish
import org.dbs.ext.GrpcFuncs.fmInTransaction
import org.dbs.ext.GrpcFuncs.fmRab
import org.dbs.ext.GrpcFuncs.fmStart
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.grpc.ext.ResponseAnswer.noErrors
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.GrpcResponse
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.MonoRAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateEmail
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.service.validator.GrpcValidators.validateOptionalField
import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.*
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import org.dbs.cm.client.CreateOrUpdatePlayerRequest as REQ
import org.dbs.cm.client.CreatePlayerResponse as RESP
import org.dbs.cm.client.CreatedPlayerDto as ENT

object GrpcCreateOrUpdatePlayer {

    private val playerDao by lazy { findService(PlayerDao::class) }
    suspend fun CmGrpcService.createOrUpdatePlayerInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get()
    ): RESP =
        request.run dto@{

            validateRemoteAddress(remoteAddress)
            val entityBuilder by lazy { ENT.newBuilder() }

            buildGrpcResponse({
                it.run {
                    val player by lazy { LateInitVal<Player>() }
                    val player4update by lazy { LateInitVal<Player>() }
                    val oldLoginNull = oldLogin.grpcGetOrNull()
                    //======================================================================================================
//                fun registerEnumNotExistsException(privilegeCode: PrivilegeCode) {
//                    addErrorInfo(
//                        RC_INVALID_REQUEST_DATA,
//                        INVALID_ENTITY_ATTR,
//                        SSS_PLAYER_PRIVILEGE_CODE,
//                        "${findI18nMessage(PV_UNKNOWN_PRIVILEGE_CODE)} '($privilegeCode)"
//                    )
//                }

                    fun validateRequestData(): Boolean = run {
                        validateMandatoryField(login, LOGIN_PATTERN, SSS_PLAYER_LOGIN)
                        validateOptionalField(oldLogin, LOGIN_PATTERN, SSS_PLAYER_LOGIN)
                        validateMandatoryField(firstName, USER_FIRST_NAME_PATTERN, SSS_PLAYER_LOGIN)
                        validateMandatoryField(lastName, USER_LAST_NAME_PATTERN, SSS_PLAYER_LOGIN)
                        validateEmail(email, SSS_PLAYER_EMAIL)

                        // is update player
                        oldLoginNull?.let {
                            validateMandatoryField(it, LOGIN_PATTERN, SSS_PLAYER_OLD_LOGIN)
                        } ?: password?.let { playerPwd ->
                            validateMandatoryField(playerPwd, PASSWORD_PATTERN, SSS_PLAYER_PASSWORD)
                        } ?: addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            SSS_PLAYER_PASSWORD,
                            findI18nMessage(FLD_INVALID_PLAYER_PASSWORD)
                        )
                        noErrors()
                    }

                    fun validateNewLogin(newLogin: EntityCode): MonoRAB =
                        playerService.findPlayerByLogin(newLogin)
                            .map {
                                addErrorInfo(
                                    RC_INVALID_REQUEST_DATA,
                                    INVALID_ENTITY_ATTR,
                                    SSS_PLAYER_LOGIN,
                                    "${findI18nMessage(EXIST_PLAYER_LOGIN)} '${newLogin}'"
                                )
                            }.switchIfEmpty { toMono() }

                    fun validateNewEmail(newEmail: Email): MonoRAB =
                        playerService.findPlayerByEmail(newEmail)
                            .map {
                                addErrorInfo(
                                    RC_INVALID_REQUEST_DATA,
                                    INVALID_ENTITY_ATTR,
                                    SSS_PLAYER_EMAIL,
                                    "${findI18nMessage(EXIST_PLAYER_EMAIL)} '${newEmail}'"
                                )
                            }.switchIfEmpty { toMono() }

                    fun validateNewPlayerLoginAndEmail(): MonoRAB = fmStart {
                        val checkNewLogin = oldLoginNull?.let { it != login } ?: true
                        val checkNewEmail = oldLoginNull.isNull() || oldEmail?.let { it != email } ?: true
                        (toMono().takeUnless { checkNewLogin } ?: validateNewLogin(login))
                            .flatMap { toMono().takeUnless { checkNewEmail } ?: validateNewEmail(email) }
                            .flatMap { takeIf { noErrors() }.toMono() }
                    }

                    fun MonoRAB.findOrCreatePlayer() = fmRab {
                        playerService.findPlayerByLogin(oldLoginNull ?: login)
                            .switchIfEmpty {
                                oldLoginNull?.let {
                                    addErrorInfo(
                                        RC_INVALID_REQUEST_DATA,
                                        INVALID_ENTITY_ATTR,
                                        SSS_PLAYER_OLD_LOGIN,
                                        findI18nMessage(FLD_UNKNOWN_PLAYER_LOGIN, oldLoginNull)
                                    )
                                    empty()
                                } ?: playerService.createNewPlayer(login)
                            }
                            .map { player.hold(it); it }
                    }

                    fun updateFromDto(player: Player): Player =
                        playerService.playerMappers.updatePlayer(player, this@dto)
                            .run { player4update.hold(this) }

                    fun savePlayer(player: Player): MonoRAB = let { rab ->
                        playerDao.savePlayer(player)
                            .map { rab }
                    }

                    fun MonoRAB.save() = fmInTransaction {
                        savePlayer(updateFromDto(player.value))
                    }

                    fun MonoRAB.finishResponseEntity() = fmFinish {
                        entityBuilder
                            .setPlayerLogin(player4update.value.login)
                            .setEmail(player4update.value.email)
                    }

                    if (validateRequestData()) {
                        processGrpcResponse {
                            validateNewPlayerLoginAndEmail()
                                .findOrCreatePlayer()
                                .save()
                                .finishResponseEntity()
                        }
                    }
                    entityBuilder
                }
            })
            { grpcResponse(it) }
        }
}
