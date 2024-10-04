package org.dbs.rest.service.value

import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.ResponseDto

interface HttpReactiveGetRequest<RESP : ResponseDto, V : HttpResponseBody<RESP>>