package org.dbs.rest.service.value

import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

interface HttpReactivePostRequest<REQ : RequestDto, T : AbstractHttpRequestBody<REQ>, RESP : ResponseDto,
        V : HttpResponseBody<RESP>>
