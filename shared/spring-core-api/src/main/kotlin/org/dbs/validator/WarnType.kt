package org.dbs.validator

import org.dbs.enums.RefEnum

enum class WarnType(private val value: String, private val code: Int) : RefEnum {

    WARN("general.warn", 1), MONITORING_ERROR_STARTING_APPLICATION("app.error", 2);

    //    MONITORING_SUCCESS_STARTING_APPLICATION("app.finish", 3),
    //    MONITORING_FINISH_APPLICATION("app.finish", 4),
    //    MONITORING_ABSTRACT_ERROR("app.abstract.error", 100),
    //    MONITORING_LIVENESS("app.abstract.error", 200),
    //    MONITORING_TOKEN("app.token.exception", 1100),
    //    MONITORING_TOKEN_EXCEPTION("app.token", 1110);
    override fun getValue() = this.value

    override fun getCode() = this.code

}
