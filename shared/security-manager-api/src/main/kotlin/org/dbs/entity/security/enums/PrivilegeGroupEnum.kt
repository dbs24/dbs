package org.dbs.entity.security.enums

import org.dbs.enums.RefEnum
import org.dbs.exception.UnknownEnumException

@Deprecated("Should be replaced by org.dbs.customers.enums.PrivilegeGroupEnum  ")
enum class PrivilegeGroupEnum(
    private val groupCode: String,
    private val groupId: Int
) : RefEnum {

    //==========================================================================
    PG_SMART_SAFE_SCHOOL("PG.SMART-SAFE-SCHOOL", 100),
    PG_STORE("PG.SMART-SAFE-SCHOOL-STORE", 200),
    PG_BANKING("PG.BANKING", 300),
    //PG_CUSTOMERS("PG.CUSTOMERS", 1000)
    ;

    companion object {
        fun getEnum(groupId: Int) =
            entries.find { it.groupId == groupId } ?: throw UnknownEnumException("groupId = $groupId")

        fun getEnum(groupCode: String) =
            entries.find { it.groupCode == groupCode } ?: throw UnknownEnumException("groupCode = $groupCode")

        fun isExistEnum(groupId: Int) = entries.find { it.groupId == groupId }?.let { true } ?: false

    }

    override fun getValue() = this.name

    fun getGroupCode() = this.groupCode

    override fun getCode() = this.groupId

}
