package org.dbs.service.repo

import org.dbs.consts.ActionId
import org.dbs.entity.core.Action
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ActionRepo : CoroutineCrudRepository<Action, ActionId?>
