package org.dbs.service.convert

import org.dbs.entity.core.EntityStatusEnum
import org.dbs.service.sync.CoreEnumsSynchronizer
import org.dbs.spring.core.api.ServiceLocator.findService
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@ReadingConverter
class EntityStatusReadConverter : Converter<Int, EntityStatusEnum> {

    private val coreEnumsSynchronizer by lazy { findService(CoreEnumsSynchronizer::class)  }

    override fun convert(source: Int): EntityStatusEnum =
        coreEnumsSynchronizer.findEntityStatus(source)
}

@WritingConverter
class EntityStatusWriteConverter : Converter<EntityStatusEnum, Int> {
    override fun convert(source: EntityStatusEnum): Int = source.entityStatusId
}
