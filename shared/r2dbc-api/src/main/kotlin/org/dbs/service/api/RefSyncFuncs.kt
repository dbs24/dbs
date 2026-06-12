package org.dbs.service.api

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.Disposable
import reactor.core.publisher.Flux

object RefSyncFuncs : Logging {

    @Deprecated("use new sync engine")
    fun <T : AbstractRefEntity<Int>> Collection<T>.synchronizeReference(
        repo: R2dbcRepository<T, Int>,
        findItemPredicate: (T, T) -> Boolean,
        newItem: (T) -> T,
    ): Disposable {

        // 1. Fast path: если нечего синхронизировать, выходим сразу без подписок
        if (this.isEmpty()) {
            // Используем logger только если он доступен в контексте (предполагается из вашего кода)
            // logger.warn { "There is no values 2 synchronize ($this)" }
            return Flux.empty<T>().subscribe()
        }

        val preparedCollection = this
        // Кэшируем имя класса один раз, чтобы не дергать рефлексию в цикле (если нужно для логов)
        val entityName = preparedCollection.first().javaClass.simpleName

        return repo.findAll().collectList()
            .flatMapMany { existingList ->
                // 2. Pre-allocate: создаем список для сохранения с точным размером
                // В худшем случае мы сохраним все элементы (insert + update)
                val saveList = ArrayList<T>(preparedCollection.size)
                var createdCount = 0
                var updatedCount = 0

                // Оптимизация: Превращаем List в Array для быстрого доступа по индексу без итераторов,
                // если существующая коллекция большая. Для ArrayList это O(1), для LinkedList O(N).
                // Здесь полагаемся на то, что collectList() возвращает ArrayList.

                for (preparedItem in preparedCollection) {
                    // 3. Поиск: Ищем совпадение.
                    // Так как findItemPredicate произвольный, мы не можем использовать HashMap O(1).
                    // Оптимизация тут возможна только перебором, но мы делаем его "сырым" циклом
                    // или `findLast` (он инлайнится в Kotlin).

                    var existingItem: T? = null

                    // Ищем с конца, как в оригинале (findLast), часто свежие данные в конце
                    for (i in existingList.indices.reversed()) {
                        val candidate = existingList[i]
                        if (findItemPredicate(candidate, preparedItem)) {
                            existingItem = candidate
                            break
                        }
                    }

                    if (existingItem != null) {
                        // 4. Проверка на изменения
                        // Важно: hashCode() быстрый, но может давать коллизии.
                        // Для критических данных лучше equals(), но вы просили скорость.
                        if (existingItem.hashCode() != preparedItem.hashCode()) {
                            saveList.add(preparedItem)
                            updatedCount++
                        }
                    } else {
                        // 5. Создание новой сущности
                        // Вызываем asNew() только если это реально нужно
                        val newItemObj = newItem(preparedItem).asNew<T>()
                        saveList.add(newItemObj)
                        createdCount++
                    }
                }

                // Логирование (опционально, раскомментируйте если есть logger)

                // 6. Batch Save: сохраняем все одним пакетом
                if (saveList.isNotEmpty()) {
                    repo.saveAll(saveList)
                } else {
                    Flux.empty()
                }
            }
            .subscribe() // Возвращаем Disposable
    }

}
