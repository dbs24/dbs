package org.dbs.config

import org.dbs.consts.SpringCoreConst.PropertiesNames.DEF_PAGE_SIZE
import org.dbs.consts.SpringCoreConst.PropertiesNames.DEF_PAGE_SIZE_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAX_PAGES
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAX_PAGES_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAX_PAGE_SIZE
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAX_PAGE_SIZE_VALUE
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PageableYmlConfig {

    @Value("\${$MAX_PAGE_SIZE:$MAX_PAGE_SIZE_VALUE}")
    val maxPageSize = MAX_PAGE_SIZE_VALUE

    @Value("\${$DEF_PAGE_SIZE:$DEF_PAGE_SIZE_VALUE}")
    val defPageSize = MAX_PAGE_SIZE_VALUE

    @Value("\${$MAX_PAGES:$MAX_PAGES_VALUE}")
    val maxPages = MAX_PAGES_VALUE

}
