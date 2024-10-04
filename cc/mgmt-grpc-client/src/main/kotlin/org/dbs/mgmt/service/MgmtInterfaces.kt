package org.dbs.mgmt.service

import org.dbs.protobuf.core.MainResponse


interface MgmtInterfaces {
    fun getPlayerCredentials(playerLogin: String): MainResponse
}
