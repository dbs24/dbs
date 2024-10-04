package org.dbs.entity.security.enums

import org.dbs.consts.PrivilegeCode
import org.dbs.consts.PrivilegeId
import org.dbs.entity.security.enums.PrivilegeGroupEnum.PG_BANKING
import org.dbs.entity.security.enums.PrivilegeGroupEnum.PG_SMART_SAFE_SCHOOL
import org.dbs.entity.security.enums.PrivilegeGroupEnum.PG_STORE
import org.dbs.enums.RefEnum
import org.dbs.exception.UnknownEnumException

@Deprecated("Should be replaced by org.dbs.customers.enums.PrivilegeEnum  ")
enum class PrivilegeEnum(
    private val privilegeName: String,
    private val privilegeCode: PrivilegeCode,
    private val privilegeGroup: PrivilegeGroupEnum,
    private val privilegeId: PrivilegeId
) : RefEnum {

    //==========================================================================
    PV_USER_VIEW("VIEW_USERS", "USER.VIEW", PG_SMART_SAFE_SCHOOL, 10001001),
    PV_ROLE_VIEW("VIEW_ROLES", "ROLES.VIEW", PG_SMART_SAFE_SCHOOL, 10002002),
    PV_USER_ROLE_VIEW("USER_ROLES_VIEW", "USER_ROLES_VIEW", PG_SMART_SAFE_SCHOOL, 10002003),
    PV_MANAGER_VIEW_ALL("MANAGER_VIEW_ALL", "MANAGER_VIEW_ALL", PG_STORE, 10004001),
    PV_MANAGER_MODIFY_CATALOG("MANAGER_MODIFY_CATALOG", "MANAGER_MODIFY_CATALOG", PG_STORE, 10004002),
    PV_MANAGER_MODIFY_ALL("MANAGER_MODIFY_ALL", "MANAGER_MODIFY_ALL", PG_STORE, 10004003),
    PV_MANAGER_MODIFY_VENDORS("MANAGER_MODIFY_VENDORS", "MANAGER_MODIFY_VENDORS", PG_STORE, 10004004),
    PV_MANAGER_MODIFY_MANAGERS("MANAGER_MODIFY_MANAGERS", "MANAGER_MODIFY_MANAGERS", PG_STORE, 10004010),
//    PV_MANAGER_APPROVAL_CARDS_IN_THE_CATALOG_FROM_VENDORS(
//        "MANAGER_APPROVAL_OF_CARDS_FROM_VENDORS",
//        "MANAGER_APPROVAL_OF_CARDS_FROM_VENDORS",
//        PG_STORE,
//        10004020
//    ),
    PV_FIN_MANAGER_VIEW_ALL("FIN_MANAGER_VIEW_ALL", "FIN_MANAGER_VIEW_ALL", PG_BANKING, 10005001),
    PV_FIN_MANAGER_MODIFY_ALL("FIN_MANAGER_MODIFY_ALL", "FIN_MANAGER_MODIFY_ALL", PG_BANKING, 10005002),
    PV_FIN_MANAGER_MODIFY_MANAGERS("FIN_MANAGER_MODIFY_MANAGERS", "FIN_MANAGER_MODIFY_MANAGERS", PG_BANKING, 10005003),
    PV_FIN_MANAGER_MODIFY_ACCOUNTS("FIN_MANAGER_MODIFY_ACCOUNTS", "FIN_MANAGER_MODIFY_ACCOUNTS", PG_BANKING, 10005004),
    PV_FIN_MANAGER_MODIFY_FEE_TAXES(
        "FIN_MANAGER_MODIFY_FEE_TAXES",
        "FIN_MANAGER_MODIFY_FEE_TAXES",
        PG_BANKING,
        10005005
    ),
    ;

    companion object {
        fun getEnum(privilegeId: PrivilegeId): PrivilegeEnum =
            entries.find { it.getCode() == privilegeId } ?: throw UnknownEnumException("privilegeId = $privilegeId")

        fun getEnum(privilegeCode: PrivilegeCode): PrivilegeEnum =
            entries.find { it.getPrivilegeCode() == privilegeCode }
                ?: throw UnknownEnumException("code = $privilegeCode")

        fun isExistEnum(privilegeCode: PrivilegeCode, pg: PrivilegeGroupEnum) = entries
            .filter { it.privilegeGroup == pg }
            .any { it.getPrivilegeCode() == privilegeCode }

        val bankPrivilegesCodes by lazy {
            entries.filter { it.privilegeGroup == PG_BANKING }
                .map(PrivilegeEnum::privilegeCode)
        }
    }

    override fun getValue() = this.privilegeName

    override fun getCode() = this.privilegeId

    fun getPrivilegeCode() = this.privilegeCode

    fun privilegeGroupId(): PrivilegeGroupEnum = this.privilegeGroup

}
