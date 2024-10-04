package org.dbs.mgmt.rest

import org.dbs.mgmt.value.user.CreateOrUpdateUserValueRequest
import org.dbs.mgmt.value.user.GetUserCredentialsRequest
import org.dbs.mgmt.value.user.UpdateUserPasswordValueRequest
import org.dbs.mgmt.value.user.UpdateUserStatusValueRequest
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequestCo
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest as R

@Service
class UserRest: H1_PROCESSOR<R>() {

    suspend fun createOrUpdateUser(request: R) = request.doRequestCo {
        CreateOrUpdateUserValueRequest(it).buildResponse(this@UserRest)
    }

    suspend fun updateUserStatus(request: R) = request.doRequestCo {
        UpdateUserStatusValueRequest(it).buildResponse(this@UserRest)
    }

    suspend fun updateUserPassword(request: R) = request.doRequestCo {
        UpdateUserPasswordValueRequest(it).buildResponse(this@UserRest)
    }

    suspend fun getUserCredentials(request: R) = request.doRequestCo {
        GetUserCredentialsRequest(it).buildResponse(this@UserRest)
    }
}
