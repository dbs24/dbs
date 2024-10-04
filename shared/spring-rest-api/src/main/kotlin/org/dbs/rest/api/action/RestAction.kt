package org.dbs.rest.api.action

import org.dbs.enums.RefEnum

enum class RestAction(private val value: String, private val code: Int) : RefEnum {
    NO_ACTION("action.empty", 1),
    CREATE_ENTITY("entity.crete", 1000000),
    MODIFY_ENTITY("entity.modify", 1000001);

    //==========================================================================
    override fun getValue() = this.value

    override fun getCode() = this.code

}