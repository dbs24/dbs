package org.dbs.analyst.repo.sql

//-------------------------------------------------------LIST-----------------------------------------------------------

const val FROM_AND_WHERE_FOR_CUSTOMERS = """
    FROM core_customers cc,
         core_entities ce
    WHERE cc.customer_id = ce.entity_id
      AND CASE
              WHEN :STATUS IS NOT NULL THEN ce.entity_status_id = :STATUS
              ELSE TRUE
          END
      AND CASE
              WHEN :FIRST_NAME IS NOT NULL THEN LOWER(cc.first_name) LIKE :FIRST_NAME
              ELSE TRUE
          END
      AND CASE
              WHEN :LAST_NAME IS NOT NULL THEN LOWER(cc.last_name) LIKE :LAST_NAME
              ELSE TRUE
          END
      AND CASE
              WHEN :EMAIL IS NOT NULL THEN LOWER(cc.email) LIKE :EMAIL
              ELSE TRUE
          END
      AND CASE
              WHEN :PHONE IS NOT NULL THEN LOWER(cc.phone) LIKE :PHONE
              ELSE TRUE
          END
      AND CASE
              WHEN :LOGIN IS NOT NULL THEN LOWER(cc.login) LIKE :LOGIN
              ELSE TRUE
          END
    """

const val SELECT_COUNT_CUSTOMERS = """
    SELECT COUNT(*) $FROM_AND_WHERE_FOR_CUSTOMERS
"""

const val SELECT_CUSTOMERS_FIELDS = """   
    SELECT cc.login,
           cc.email,
           cc.phone,
           cc.first_name,
           cc.last_name,
           ce.entity_status_id,
           cc.avatar
    $FROM_AND_WHERE_FOR_CUSTOMERS
    LIMIT :#{#pageable.pageSize}
    OFFSET :#{#pageable.offset}
    """
