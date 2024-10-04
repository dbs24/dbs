package org.dbs.auth.server.repo.sql

const val SQL_SELECT_ACCESS_JWT = """
    SELECT *
    FROM tkn_issued_jwt
    WHERE jwt = :JWT
    ORDER BY jwt_id
    LIMIT 1
"""

const val SQL_SELECT_ACTUAL_ACCESS_JWT = """
    SELECT *
    FROM tkn_issued_jwt
    WHERE jwt = :JWT
      AND is_revoked = FALSE
    ORDER BY jwt_id
    LIMIT 1
"""

const val SQL_SELECT_REFRESH_JWT = """
    SELECT *
    FROM tkn_refresh_jwt
    WHERE jwt = :JWT
      AND is_revoked = FALSE
    ORDER BY jwt_id
    LIMIT 1
"""
