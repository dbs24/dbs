package org.dbs.service.api

import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.spring.ref.AbstractRefEntity
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.fromIterable
import java.util.concurrent.atomic.AtomicInteger

object RefSyncFuncs : Logging {
    fun <T : AbstractRefEntity<Int>> Flux<T>.synchronizeReference(
        repo: R2dbcRepository<T, Int>,
        findItemPredicate: (T, T) -> Boolean,
        newItem: (T) -> T,
    ) = this.run {
        val saveCollection = createCollection<T>()
        val itemsUpdated = AtomicInteger(0)
        val itemsCreated = AtomicInteger(0)
        repo.saveAll(
            repo.findAll().collectList()
                .flatMap { existsCollection ->
                    this.collectList() // preparedCollection
                        .map {
                            it.forEach { preparedItem ->
                                existsCollection.findLast { existItem ->
                                    findItemPredicate(
                                        existItem,
                                        preparedItem
                                    )
                                }
                                    ?.let { letIt ->
                                        if (letIt.hashCode() != preparedItem.hashCode()) {
                                            saveCollection.add(preparedItem).also { itemsUpdated.incrementAndGet() }
                                        }
                                    } ?: run {
                                    saveCollection.add(newItem(preparedItem).asNew<T>().also {
                                        logger.debug("new reference record: $it")
                                        itemsCreated.incrementAndGet()
                                    })
                                }
                            }
                            if (it.isNotEmpty()) {
                                logger.debug {
                                    "${it[0].javaClass.simpleName}: " +
                                            "references items update (${saveCollection.size} items), " +
                                            "created: ${itemsCreated.get()}, updated: ${itemsUpdated.get()}"
                                }
                            } else {
                                logger.warn { "There is no values 2 synchronize ($this)" }
                            }
                            saveCollection
                        }
                }
                .flatMapMany {
                    fromIterable(saveCollection)
                }
        ).count()
            .subscribeMono()
    }
}
