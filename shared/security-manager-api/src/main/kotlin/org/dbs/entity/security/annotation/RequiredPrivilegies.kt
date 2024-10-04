package org.dbs.entity.security.annotation

import org.dbs.entity.security.enums.PrivilegeEnum


@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RequiredPrivilegies(
    val privilegies: Array<PrivilegeEnum>
)