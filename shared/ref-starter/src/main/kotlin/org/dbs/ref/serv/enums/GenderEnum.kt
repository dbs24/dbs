package org.dbs.ref.serv.enums

import org.dbs.application.core.service.funcs.TestFuncs
import org.dbs.application.core.service.funcs.TestFuncs.selectFrom
import org.dbs.exception.UnknownEnumException

enum class GenderEnum(
    private val code: String
) {
    MALE("M"),
    FEMALE("F"),
    OTHER("O"),
    ;

    companion object {
        fun getEnum(code: String): GenderEnum =
            entries.also {
                System.out.println ("################################### find '$code'")
            }.first { it.code == code } ?: error("Unknown gender - $code")

        fun getByName(name: String): GenderEnum =
            entries.first { it.name == name }

        fun getByNameNullable(name: String): GenderEnum? =
            entries.firstOrNull { it.name == name }

        fun isExistsEnum(code: String) = entries.any { it.code == code }

        val genders by lazy { GenderEnum.entries.map(GenderEnum::code) }

        fun getRandomGender() = selectFrom(genders)

    }

    fun getCode() = this.code
}
