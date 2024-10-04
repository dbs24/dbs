package org.dbs.customer.service

import org.dbs.protobuf.core.MainResponse


interface CustomerInterfaces {
    fun getUserCredentials(playerLogin: String): MainResponse
}
