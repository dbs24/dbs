package org.dbs.spring.core.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service

@Service
@ConfigurationProperties("config.references")
class ReferencesConfig {
    var autoSynchronize: Boolean = true
}
