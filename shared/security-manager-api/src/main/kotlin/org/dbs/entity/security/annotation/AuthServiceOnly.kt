package org.dbs.entity.security.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.PROPERTY


@Target(PROPERTY)
@Retention(RUNTIME)
@MustBeDocumented
annotation class AuthServiceOnly(
    //val actions: Array<ActionCodeEnum>
    val authServiceOnly: Boolean = true
)