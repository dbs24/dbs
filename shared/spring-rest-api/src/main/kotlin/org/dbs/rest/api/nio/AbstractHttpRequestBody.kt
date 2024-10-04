package org.dbs.rest.api.nio

import org.dbs.consts.RequestVersion
import org.dbs.consts.SysConst.VERSION_1_0_0

abstract class AbstractHttpRequestBody<T : RequestDto>(
    override val version: RequestVersion = VERSION_1_0_0
) : HttpRequestBody<T>
