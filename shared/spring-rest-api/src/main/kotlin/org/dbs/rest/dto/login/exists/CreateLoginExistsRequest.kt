package org.dbs.rest.dto.login.exists

import org.dbs.spring.core.api.PostRequestBody
import org.dbs.consts.SysConst.VERSION_1_0_0
import org.dbs.rest.api.action.SimpleActionInfo


class CreateLoginExistsRequest(
    override val version: String = VERSION_1_0_0,
    override val entityAction: SimpleActionInfo,
    override val entityInfo: LoginExistsInfo
) : PostRequestBody
