package org.dbs.goods.repo.sql

const val SELECT_MANAGERS_PASSWORDS =
    """
            SELECT p.password_hash
              FROM core_manager_passwords p
              WHERE p.manager_id = :ID
              ORDER BY password_id DESC
        """