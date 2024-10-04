package org.dbs.service.consts

import org.dbs.consts.EntityId
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.dbs.entity.core.EntityActionEnum
import reactor.core.publisher.Mono


typealias ActionExec<TE> = (Collection<TE>, EntityActionEnum, IpAddress, StringNote) -> Mono<EntityId>
