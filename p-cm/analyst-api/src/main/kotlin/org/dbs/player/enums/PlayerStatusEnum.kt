package org.dbs.player.enums

import org.dbs.consts.EntityConsts.EntityStatuses.ACTUAL
import org.dbs.consts.EntityConsts.EntityStatuses.BANNED
import org.dbs.consts.EntityConsts.EntityStatuses.CLOSED
import org.dbs.enums.RefEnum

enum class PlayerStatusEnum(
    private val statusId: Int,
    private val statusCode: String
) : RefEnum {
    PS_ACTUAL(100, ACTUAL),
    //================================================================================================
    PS_CLOSED(110, CLOSED),
    PS_BANNED(120, BANNED);

    companion object {
        fun getEnum(id: Int) = entries.find { it.statusId == id } ?: error("unknown statusId = $id")

        fun getEnum(code: String) = entries.find { it.statusCode == code } ?: error("unknown status = $code")

        fun isExistEnum(id: Int) = entries.find { it.statusId == id }?.let { true } ?: false

    }

    override fun getValue() = this.name

    fun getStatusCode() = this.statusCode

    override fun getCode() = this.statusId
}
