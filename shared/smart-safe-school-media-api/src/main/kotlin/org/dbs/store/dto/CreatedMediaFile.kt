package org.dbs.store.dto

import org.dbs.spring.core.api.EntityInfo

data class CreatedMediaFile(
    val filePath: String,
) : EntityInfo
