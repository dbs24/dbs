package org.dbs.service.repo

import org.dbs.entity.core.ActionCode
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ActionCodeRepository : CoroutineCrudRepository<ActionCode, Int>