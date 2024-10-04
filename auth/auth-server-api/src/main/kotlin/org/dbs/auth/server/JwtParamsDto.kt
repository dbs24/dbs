package org.dbs.auth.server

data class JwtParamsDto(
    val userId: Long,
    val userLogin: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val email: String?,
    val address: String?,
    val schoolId: String?,
    val roles: String,
    val schoolTimeZone: Int
)
