package org.dbs.spring.core.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service

@Service
@ConfigurationProperties("server")
class ServerConfig {
    var devMode: Boolean = false
    var junitMode: Boolean = false
}
