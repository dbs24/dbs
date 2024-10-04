package org.dbs.auth.server.enums

import org.dbs.auth.server.consts.ApplicationId
import org.dbs.consts.ApplicationName
import org.dbs.consts.SysConst.APP_CHESS_COMMUNITY
import org.dbs.exception.UnknownEnumException

enum class ApplicationEnum(
    private val applicationId: ApplicationId,
    private val applicationName: ApplicationName
) {
    CHESS_COMMUNITY(1000, APP_CHESS_COMMUNITY),
    ;

    companion object {
        //==================================================================================================================
        //@get:Synchronized

        fun getEnum(applicationId: Int): ApplicationEnum =
            entries.find { it.applicationId == applicationId }
                ?: throw UnknownEnumException("applicationId = $applicationId")

        fun isExistEnum(id: Int) = entries.find { it.getApplicationId() == id }?.let { true } ?: false

    }

    fun getApplicationName() = this.applicationName
    fun getApplicationId() = this.applicationId
}
