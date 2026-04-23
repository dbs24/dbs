package org.dbs.spring.core.api

abstract class AbstractApplicationConfig(
    val serverCfg: ServerConfig,
    val webClientCfg: WebClientConfig,
    val restfulCfg: RestfulConfig,
    val referencesCfg: ReferencesConfig,
) : AbstractApplicationBean() {

    val devMode get() = serverCfg.devMode
    val junitMode get() = serverCfg.junitMode

    val maxConnections get() = webClientCfg.maxConnections
    val testConnection get() = webClientCfg.testConnection
    val maxIdleTime get() = webClientCfg.maxIdleTime
    val maxLifeTime get() = webClientCfg.maxLifeTime
    val pendingAcquireTimeout get() = webClientCfg.pendingAcquireTimeout
    val evictInBackground get() = webClientCfg.evictInBackground

    val queryMaxTimeExec get() = restfulCfg.query.maxTimeExec
    val printEntityId get() = restfulCfg.message.printEntityId

    val autoSynchronize get() = referencesCfg.autoSynchronize
}
