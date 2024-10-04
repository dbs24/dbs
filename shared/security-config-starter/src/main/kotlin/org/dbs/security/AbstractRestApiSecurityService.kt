package org.dbs.security

import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.PrivilegeCode
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_BACK_DOOR_JWT
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_BACK_DOOR_JWT
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.entity.security.annotation.AuthServiceOnly
import org.dbs.entity.security.annotation.RequiredPrivilegies
import org.dbs.entity.security.enums.PrivilegeEnum
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

abstract class AbstractRestApiSecurityService(routesObjectClass: KClass<*>) : AbstractApplicationService(),
    RestApiSecurityService {

    @Value("\${$CONFIG_BACK_DOOR_JWT:$VALUE_BACK_DOOR_JWT}")
    protected val backDoorJwt: String = EMPTY_STRING

    protected val requiredAuthServiceOnlyList: MutableCollection<String> = createCollection()
    private val requiredPrivilegesList: MutableCollection<Pair<String, PrivilegeEnum>> = createCollection()

    init {
        routesObjectClass
            .declaredMemberProperties
            .forEach {
                // AuthServiceOnly
                it.findAnnotation<AuthServiceOnly>()?.let { _ -> requiredAuthServiceOnlyList.add(it.call() as String) }
                // RequiredPrivilege
                it.findAnnotation<RequiredPrivilegies>()?.let { rp ->
                    rp.privilegies.iterator().forEach { privilegeEnum ->
                        requiredPrivilegesList.add(Pair(it.call() as String, privilegeEnum))
                    }
                }
            }
    }

    protected fun getRequiredPrivilegesList(path: String): Collection<PrivilegeCode> =
        requiredPrivilegesList.asSequence().filter { it.first == path }.map { it.second.getPrivilegeCode() }.toList()

    protected fun useBackDoor(path: String, token: String) = token.endsWith(backDoorJwt).also {
        if (it) logger.warn("$path: blackDoor is used 4 access point")
    }
    override fun initialize() = super.initialize().also {
        logger.debug { "authServiceList = ${requiredAuthServiceOnlyList.size} element(s): $requiredAuthServiceOnlyList" }
        logger.debug { "privilegesList = ${requiredPrivilegesList.size} element(s): $requiredPrivilegesList" }
    }
}
