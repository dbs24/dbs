package org.dbs.tree.rest

import org.dbs.tree.mapper.UserMappers.toCommand
import org.dbs.tree.mapper.UserMappers.toUserDto
import org.dbs.tree.service.UserService
import org.dbs.user.dto.user.CreateOrUpdateUserDto
import org.dbs.user.dto.user.CreatedUserDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class RestUserController(private val service: UserService) {

    @PostMapping("/createOrUpdate")
    suspend fun createOrUpdate(@RequestBody dto: CreateOrUpdateUserDto): CreatedUserDto =
        service.createOrUpdateUser(dto.toCommand()).toUserDto()
}