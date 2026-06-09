package org.dbs.security

import org.dbs.validator.ErrorInfo


interface LoginService {

    suspend fun login(user: String, password: String? = null): Collection<ErrorInfo>

}