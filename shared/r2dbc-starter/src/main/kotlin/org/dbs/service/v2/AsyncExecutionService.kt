package org.dbs.service.v2

import org.dbs.consts.SpringCoreConst.PropertiesNames.ASYNC_ALG_STORE_ENABLED
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

// Legacy async execution service — replaced by EntityActionEventService (@EventListener/@Async).
// Kept as a placeholder; will be removed once all modules adopt the ActionEvent approach.
@Service
@Lazy(false)
@ConditionalOnProperty(name = [ASYNC_ALG_STORE_ENABLED], havingValue = STRING_TRUE)
class AsyncExecutionService : AbstractApplicationService()
