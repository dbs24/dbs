package org.dbs.outOfService.mapper

import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toLong
import org.dbs.application.core.service.funcs.LongFuncs.toLocalDateTime
import org.dbs.consts.OperDateDtoNull
import org.dbs.consts.StringNoteNull
import org.dbs.outOfService.model.CoreOutOfService
import org.dbs.outOfService.model.CoreOutOfServiceHist
import org.dbs.outOfService.service.CoreOutOfServiceService
import java.time.LocalDateTime.now

object CoreOutOfServiceMappers {

    fun CoreOutOfServiceService.updateCoreOutOfService(
        src: CoreOutOfService,
        dateStart: OperDateDtoNull,
        dateFinish: OperDateDtoNull,
        note: StringNoteNull
    ): CoreOutOfService =
        src.copy(
            id = src.id,
            createDate = src.createDate,
            actualDate = now(),
            serviceDateStart = dateStart.takeIf { it != 0L }.toLocalDateTime(),
            serviceDateFinish = dateFinish.takeIf { it != 0L }.toLocalDateTime(),
            note = note
        )

    fun CoreOutOfServiceService.createHist(src: CoreOutOfService) =
        CoreOutOfServiceHist(
            id = now().toLong(),
            actualDate = src.actualDate,
            serviceDateStart = src.serviceDateStart,
            serviceDateFinish = src.serviceDateFinish,
            note = src.note
        )
}
