package org.dbs.sandbox.rest

import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequestCo
import org.dbs.sandbox.value.invite.CreateOrUpdateInviteValueRequest
import org.dbs.sandbox.value.invite.UpdateInviteStatusValueRequest
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest as R

@Service
class InviteRest: H1_PROCESSOR<R>() {

    suspend fun createOrUpdateInvite(request: R) = request.doRequestCo {
        CreateOrUpdateInviteValueRequest(it).buildResponse(this@InviteRest)
    }

    suspend fun updateInviteStatus(request: R) = request.doRequestCo {
        UpdateInviteStatusValueRequest(it).buildResponse(this@InviteRest)
    }
}
