package org.dbs.auth.verify.consts

import org.dbs.consts.CKS
import org.dbs.consts.CTX
import org.dbs.consts.SpringCoreConst.DELIMITER
import org.dbs.consts.SysConst.EMPTY_STRING

object AuthVerifyConsts {
    // player
    val CK_VERIFY_JWT_PROCEDURE: CKS = CTX.key("verifyJwt")


    const val YML_AUTH_A1_CACHE = "config.cache.a1.enabled"

    val AUTH_VERIFY_SET = mapOf(
        "enabled A1 cache " to YML_AUTH_A1_CACHE,
        DELIMITER to EMPTY_STRING
    )


}
