package org.dbs.rest

import org.dbs.component.JwtSecurityService
import org.dbs.dto.jwt.LoginUserDto
import org.dbs.dto.jwt.LoginUserResponseDto
import org.dbs.mapper.JwtMappers.toCommand
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/security")
class RestJwtController(private val jwtSecurityService: JwtSecurityService) {

    @PostMapping("/login")
    suspend fun login(@RequestBody dto: LoginUserDto): LoginUserResponseDto =
        jwtSecurityService.loginUser(dto.toCommand())
}
