package org.dbs.service.dao

import com.mongodb.reactivestreams.client.MongoClient
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.dbs.consts.SysConst.EMPTY_STRING
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service

@Service
class MongoDao(val reactiveMongoTemplate: ReactiveMongoTemplate, val mongoClient: MongoClient) :
    DaoAbstractApplicationService() {

    @Value("\${spring.application.name}")
    private val applicationName = EMPTY_STRING

    override fun initialize() {
        super.initialize()
        logger.debug("$applicationName: mongoClient is $mongoClient")
        logger.debug("reactiveMongoTemplate is ${reactiveMongoTemplate.javaClass.simpleName}")
    }

}
