package org.dbs.goods.service

import org.dbs.protobuf.core.MainResponse


interface MgmtInterfaces {
    fun getUserCredentials(playerLogin: String): MainResponse
}
