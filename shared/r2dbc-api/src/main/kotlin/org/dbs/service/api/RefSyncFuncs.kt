package org.dbs.service.api

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.Disposable
import reactor.core.publisher.Flux
import java.util.concurrent.atomic.AtomicInteger

object RefSyncFuncs : Logging {

    fun <T : AbstractRefEntity<Int>> Collection<T>.synchronizeReference(
        repo: R2dbcRepository<T, Int>,
        findItemPredicate: (T, T) -> Boolean,
        newItem: (T) -> T,
    ): Disposable {
        val itemsUpdated = AtomicInteger(0)
        val itemsCreated = AtomicInteger(0)

        // Входящая коллекция уже доступна как `this`
        val preparedCollection = this

        return repo.findAll().collectList()
            .flatMapMany { existsCollection ->
                val saveCollection = ArrayList<T>()

                // Синхронный перебор, так как preparedCollection уже в памяти
                preparedCollection.forEach { preparedItem ->
                    val existingItem = existsCollection.findLast { existItem ->
                        findItemPredicate(existItem, preparedItem)
                    }

                    existingItem?.apply {
                        // Если хеш изменился — добавляем на обновление
                        if (existingItem.hashCode() != preparedItem.hashCode()) {
                            saveCollection.add(preparedItem)
                            itemsUpdated.incrementAndGet()
                        }
                    } ?: run {
                        // Если не найдено — создаем новую запись
                        val createdItem = newItem(preparedItem).asNew<T>().also {
                            logger.debug("new reference record: $it")
                            itemsCreated.incrementAndGet()
                        }
                        saveCollection.add(createdItem)
                    }
                }

                // Логирование итогов
                if (preparedCollection.isNotEmpty()) {
                    logger.debug {
                        "${preparedCollection.first().javaClass.simpleName}: " +
                                "references items update (${saveCollection.size} items), " +
                                "created: ${itemsCreated.get()}, updated: ${itemsUpdated.get()}"
                    }
                } else {
                    logger.warn { "There is no values 2 synchronize ($preparedCollection)" }
                }

                // Сохраняем результат (или возвращаем пустой Flux, если нечего сохранять)
                if (saveCollection.isNotEmpty()) {
                    repo.saveAll(saveCollection)
                } else {
                    Flux.empty()
                }
            }
            .count()
            .subscribe()
    }
}
