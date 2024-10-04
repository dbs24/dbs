package org.dbs.auth.server.consts

import org.dbs.consts.CKS
import org.dbs.consts.CTX

object GrpcConsts {
    // player
    val CK_PLAYER_LOGIN_PROCEDURE: CKS = CTX.key("playerLogin")
    val CK_FIND_JWT_PROCEDURE: CKS = CTX.key("findJwt")

}
