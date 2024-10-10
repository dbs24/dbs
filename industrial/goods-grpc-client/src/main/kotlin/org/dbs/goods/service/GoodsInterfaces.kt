package org.dbs.goods.service

import org.dbs.protobuf.core.MainResponse


interface GoodsInterfaces {
    fun getUserCredentials(userLogin: String): MainResponse
}
