package org.dbs.analyst.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class SolutionMappers(
    private val objectMapper: ObjectMapper,
    private val passwordEncoder: PasswordEncoder
) : Logging {
//    fun updateSolution(src: Solution, srcDto: CreateOrUpdateSolutionDto): Solution =
//        src.copy(
//            login = srcDto.login,
//            email = srcDto.email,
//            lastName = srcDto.lastName,
//            firstName = srcDto.firstName,
//            //status = srcDto.avatarImage?.let { objectMapper.writeValueAsString(it) },
//        )


//    fun createHist(src: Solution) =
//        SolutionHist(
//            actualDate = src.getCoreEntity().modifyDate,
//            solutionId = src.solutionId,
//            login = src.login,
//            email = src.email,
//            phone1 = src.phone1,
//            phone2 = src.phone2,
//            gender = src.gender,
//            lastName = src.lastName,
//            firstName = src.firstName,
//            birthDate = src.birthDate,
//            country = src.country,
//            region = src.region,
//            avatarImg = src.status
//        )

}
