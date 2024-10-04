package org.dbs.player.enums

import org.dbs.consts.EntityConsts.EntityStatuses.ACTUAL
import org.dbs.consts.EntityConsts.EntityStatuses.CANCELLED
import org.dbs.consts.EntityConsts.EntityStatuses.INVALID
import org.dbs.consts.EntityConsts.EntityStatuses.IN_PROGRESS
import org.dbs.enums.RefEnum

enum class SolutionStatusEnum(
    private val statusId: Int,
    private val statusCode: String
) : RefEnum {
    SS_ACTUAL(1000, ACTUAL),
    SS_IN_PROGRESS(1100, IN_PROGRESS),
    //================================================================================================
    SS_CANCELLED(1200, CANCELLED),
    SS_INVALID_FEN_POSITION(2000, INVALID),
    ;

    companion object {
        fun getEnum(id: Int) = entries.find { it.statusId == id } ?: error("unknown statusId = $id")

        fun getEnum(code: String) = entries.find { it.statusCode == code } ?: error("unknown status = $code")

        fun isExistEnum(id: Int) = entries.find { it.statusId == id }?.let { true } ?: false

    }

    override fun getValue() = this.name

    fun getStatusCode() = this.statusCode

    override fun getCode() = this.statusId
}
