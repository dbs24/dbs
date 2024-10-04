package org.dbs.rest.api.nio

import org.dbs.consts.RequestVersion
import java.io.Serializable

sealed interface HttpRequestBody<T : RequestDto> : Serializable {
    val version: RequestVersion
    val requestBodyDto: T
}
