package org.dbs.mgmt.service

import org.dbs.spring.core.api.AbstractApplicationConfig
import org.dbs.spring.core.api.ReferencesConfig
import org.dbs.spring.core.api.RestfulConfig
import org.dbs.spring.core.api.ServerConfig
import org.dbs.spring.core.api.WebClientConfig
import org.springframework.stereotype.Component

@Component
class ApplicationConfig(
    serverConfig: ServerConfig,
    webClientConfig: WebClientConfig,
    restfulConfig: RestfulConfig,
    referencesConfig: ReferencesConfig,
) : AbstractApplicationConfig(serverConfig, webClientConfig, restfulConfig, referencesConfig)
