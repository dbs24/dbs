package org.dbs.tasker.config

import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.config.MainApplicationConfig
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
class TaskerConfig : MainApplicationConfig()
