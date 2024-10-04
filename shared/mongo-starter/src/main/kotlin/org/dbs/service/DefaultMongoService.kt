package org.dbs.service

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.spring.core.api.PublicApplicationBean
import org.dbs.consts.SysConst.ALL_PACKAGES
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.SLASH
import org.dbs.service.consts.MongoConsts.MONGO_URI
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import reactor.core.publisher.Mono.just

@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
@EnableReactiveMongoRepositories(basePackages = [ALL_PACKAGES], basePackageClasses = [DefaultMongoService::class])
class DefaultMongoService : AbstractReactiveMongoConfiguration(), PublicApplicationBean {

    @Value("\${spring.data.mongodb.uri}")
    private val dbUri = EMPTY_STRING

    @Bean
    fun auditorAware(): ReactiveAuditorAware<String>? =
        ReactiveAuditorAware { just("hantsy") }
//    val mongoClient: MongoClient by lazy {
//        logger.debug("init reactive mongo database: $dbName")
//        super.reactiveMongoClient()
//    }

    @Bean
    override fun reactiveMongoClient(): MongoClient = MongoClients.create(dbUri)

    override fun getDatabaseName() = dbUri.run {

        val beginPos = this.indexOf(SLASH, MONGO_URI.length + 1) + 1
        val endPos = this.indexOf("?").let { if (it > 0) it else this.length }

        this.substring(beginPos, endPos).also {
            logger.debug("initialize mongo db '$it' ($dbUri)")
        }
    }

    override fun initialize() {
        //TODO("Not yet implemented")
    }

    override fun shutdown() {
        //TODO("Not yet implemented")
    }
}
