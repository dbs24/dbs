package org.dbs.rest.dto.login

import org.dbs.spring.core.api.EntityInfo
import org.dbs.consts.SysConst.SECURED
import org.dbs.consts.SysConst.STRING_NULL

data class LoginInfo(
    var accessToken: String? = STRING_NULL,
    val userLogin: String? = STRING_NULL,
    val userPass: String? = STRING_NULL,
    val appPackage: String? = STRING_NULL,
    val appVersion: String? = STRING_NULL
) : EntityInfo {
    override fun toString() =
        "LoginInfo(accessToken=$accessToken, userLogin=$userLogin, userPass=$SECURED, " +
                "appPackage=$appPackage, appVersion=$appVersion)"
}
