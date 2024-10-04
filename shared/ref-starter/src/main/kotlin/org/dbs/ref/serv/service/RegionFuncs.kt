package org.dbs.ref.serv.service

import org.dbs.application.core.service.funcs.Patterns.REGION_CODE_PATTERN
import org.dbs.ref.serv.enums.RegionEnum.Companion.isExistRegion

object RegionFuncs {
    fun String.isValidRegionCode() = REGION_CODE_PATTERN.matcher(this).matches() && isExistRegion(
        this
    )
}