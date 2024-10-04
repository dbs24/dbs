package org.dbs.enums

import java.util.Arrays.stream

interface RefEnum : AbstractEnum {
    fun getValue(): String
    fun getCode(): Int

    fun findByCode(refEnum: Array<RefEnum>, code: Int) =
        stream(refEnum).filter { it.getCode() == code }
            .findFirst()
            .orElseThrow { RuntimeException("${javaClass.canonicalName}: Unknown enum code - ($code)") }
}