package org.dbs.spring.core.api

interface PostRequestBody : RequestBody {
    val version: String
    val entityInfo: EntityInfo
    val entityAction: FlatEntityAction
}
