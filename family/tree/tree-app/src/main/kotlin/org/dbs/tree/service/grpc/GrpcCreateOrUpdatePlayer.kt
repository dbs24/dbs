package org.dbs.tree.service.grpc

import org.dbs.api.JobKey

object GrpcCreateOrUpdateUser {



    private enum class JobKeyImp : JobKey {
        JK_FIND_OR_CREATE_USER,
    }
//
//    suspend fun MgmtGrpcService.createOrUpdateUserInternal(
//        request: REQ,
//        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
//    ): RESP = run {
//        validateRemoteAddress(remoteAddress)
//        buildGrpcResponse {
//            ResponseCoProcessorWrapper(object : ResponseCoProcessor<REQ, ENT.Builder> {
//                // main flow
//                ////////////////////////////////////////////////////////////////////////////////////////////////////////
//                override suspend fun execute() = executeIternal {
//                    validateNewLogin()
//                    findOrCreateUser()
//                    saveEntity()
//                }
//
//                ////////////////////////////////////////////////////////////////////////////////////////////////////////
//                private val user by lazy { LateInitVal<User>("user") }
//                private val user4Update by lazy { LateInitVal<User>("user4update") }
//                private val oldUserLogin by lazy { request.oldLogin.grpcGetOrNull() }
//                private var newUserId : UserId? = null
//
//                //======================================================================================================
//                override fun isValidDto() = request.run {
//                    with(rab) {
//                        validateMandatoryField(login, LOGIN_PATTERN, SSS_USER_LOGIN)
//                        validateOptionalField(oldLogin, LOGIN_PATTERN, SSS_USER_LOGIN)
//                        validateOptionalField(firstName, USER_FIRST_NAME_PATTERN, SSS_USER_FIRST_NAME)
//                        validateOptionalField(middleName, USER_LAST_NAME_PATTERN, SSS_USER_MIDDLE_NAME)
//                        validateOptionalField(lastName, USER_LAST_NAME_PATTERN, SSS_USER_LAST_NAME)
//                        validateOptionalEmail(email, SSS_USER_EMAIL)
//                        // is update user
//                        oldUserLogin?.let {
//                            validateMandatoryField(it, LOGIN_PATTERN, SSS_USER_OLD_LOGIN)
//                        } ?: password?.let { userPwd ->
//                            validateMandatoryField(userPwd, PASSWORD_PATTERN, SSS_USER_PASSWORD)
//                        } ?: addErrorInfo(
//                            RC_INVALID_REQUEST_DATA,
//                            INVALID_ENTITY_ATTR,
//                            SSS_USER_PASSWORD,
//                            findI18nMessage(FLD_INVALID_USER_PASSWORD)
//                        )
//                        noErrors()
//                    }
//                }
//
//                //------------------------------------------------------------------------------------------------------
//                suspend fun validateNewLogin(newLogin: EntityCode) =
//                    userService.findUserByLogin(newLogin)
//                        ?.apply {
//                            rab.addErrorInfo(
//                                RC_INVALID_REQUEST_DATA,
//                                INVALID_ENTITY_ATTR,
//                                SSS_USER_LOGIN,
//                                "${findI18nMessage(EXIST_USER_LOGIN)} '${newLogin}'"
//                            )
//                        }
//
//                //------------------------------------------------------------------------------------------------------
//                suspend fun validateNewEmail(newEmail: Email) =
//                    userService.findUserByEmail(newEmail)
//                        ?.apply {
//                            rab.addErrorInfo(
//                                RC_INVALID_REQUEST_DATA,
//                                INVALID_ENTITY_ATTR,
//                                SSS_USER_EMAIL,
//                                "${findI18nMessage(EXIST_USER_EMAIL)} '${newEmail}'"
//                            )
//                        }
//
//                //------------------------------------------------------------------------------------------------------
//                private fun updateFromDto(user: User): User =
//                    userService.updateUser(user, request)
//                        .run { user4Update.init(this) }
//
//                //------------------------------------------------------------------------------------------------------
//                private suspend fun saveUser(user: User): User =
//                    userService.saveUser(user)
//
//                //------------------------------------------------------------------------------------------------------
//                private suspend fun validateNewLogin() =
//                    request.apply {
//                        val checkNewLogin = oldUserLogin?.let { it != login } != false
//                        val checkNewEmail = oldUserLogin.isNull() || oldEmail?.let { it != email } ?: true
//                        if (checkNewLogin) validateNewLogin(login)
//                        if (checkNewEmail && email.isNotBlank()) validateNewEmail(email)
//                    }
//
//                //------------------------------------------------------------------------------------------------------
//                private suspend fun findOrCreateUser() = launchJob(JK_FIND_OR_CREATE_USER) {
//                    userService.findUserByLogin(oldUserLogin ?: request.login)
//                        ?.apply { user.init(this) }
//                        ?: oldUserLogin?.let {
//                            rab.addErrorInfo(
//                                RC_INVALID_REQUEST_DATA,
//                                INVALID_ENTITY_ATTR,
//                                SSS_USER_OLD_LOGIN,
//                                findI18nMessage(FLD_UNKNOWN_USER_LOGIN, it)
//                            )
//                        } ?: userService.createNewUser(request.login).apply { user.init(this) }
//                }
//
//                //------------------------------------------------------------------------------------------------------
//                suspend fun saveEntity() = launchJob(JK_SAVE, JK_FIND_OR_CREATE_USER) {
//                    userService.saveHistory(user.value)
//                        saveUser(updateFromDto(user.value))
//                            .apply { newUserId = userId }
//
//                }
//
//                //------------------------------------------------------------------------------------------------------
//                override suspend fun finishResponse(): ENT.Builder = finish(JK_SAVE, ENT.newBuilder()) {
//                    it.setUserLogin(user4Update.value.login)
//                        .setEmail(user4Update.value.email)
//                        .setStatus(user4Update.value.status.entityStatusName)
//                }
//
//                //------------------------------------------------------------------------------------------------------
//
//                override fun registryAction(duration: Long) {
//                    eventPublisher.value.registryEvent(
//                        requireNotNull(newUserId ?: user.value.userId) { "userId must be set after save" },
//                        user.value.type.entityTypeId,
//                        EA_CREATE_OR_UPDATE_USER.actionCodeId,
//                        remoteAddress,
//                        request.toString(),
//                        duration
//                    )
//                }
//
//                //------------------------------------------------------------------------------------------------------
//
//                override val jobsMap by lazy { defaultJobsMap() }
//                override val rab by lazy { it }
//                override val coroutineScope by lazy { defaultCoroutineScope() }
//
//            })
//        }
//    }
}
