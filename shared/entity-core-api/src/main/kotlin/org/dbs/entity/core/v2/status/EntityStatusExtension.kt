package org.dbs.entity.core.v2.status

import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.v2.type.EntityTypeExtension.findAllowedStatusesChanges

object EntityStatusExtension {
    private fun EntityStatusEnum.isAllowedStatusUpdates(newEntityStatus: EntityStatusEnum): Boolean =
        this.let { entityStatus ->
            (entityStatus == newEntityStatus).takeIf { it } ?: let {
                val entries = entityStatus.entityType.findAllowedStatusesChanges().entries
                val notFoundAnswer = entries.isEmpty()

                notFoundAnswer.takeIf { it } ?: let {
                    entries.firstOrNull { it.key == entityStatus }
                        ?.let { it.value.any { allowedAcc -> allowedAcc == newEntityStatus } } ?: false
                }
            }
        }

    fun EntityStatusEnum.isAllowedStatusUpdate(newEntityStatus: EntityStatusEnum): String =
        if (isAllowedStatusUpdates(newEntityStatus)) EMPTY_STRING else "${this.entityType}: " +
                "entityStatus update is not allowed [$this -> $newEntityStatus]"

}
