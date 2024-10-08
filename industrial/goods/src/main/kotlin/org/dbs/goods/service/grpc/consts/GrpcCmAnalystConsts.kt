package org.dbs.goods.service.grpc.consts

import org.dbs.consts.CKI
import org.dbs.consts.CKS
import org.dbs.consts.CTX

object GrpcCmAnalystConsts {

    object ContextKeys {
        val CK_MANAGER_LOGIN: CKS = CTX.key("MANAGER_LOGIN")
        val CK_MANAGER_ID: CKI = CTX.key("MANAGER_ID")

        val CK_MANAGER_GET_CREDENTIALS_PROCEDURE: CKS = CTX.key("getManagerCredentials")
        val CK_MANAGER_UPDATE_PASSWORD_PROCEDURE: CKS = CTX.key("updateManagerPassword")

    }
}
