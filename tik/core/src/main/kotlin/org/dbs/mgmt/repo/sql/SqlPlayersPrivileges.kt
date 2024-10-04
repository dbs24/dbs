package org.dbs.mgmt.repo.sql


const val SELECT_PRIVILEGES_CODES_BY_MANAGER_ID_STATUS = """
    SELECT p.privilege_code
    FROM core_manager_privileges mp,
         core_entities e,
         core_privileges_ref p
    WHERE mp.manager_id = :M_ID
      AND mp.assign_id = e.entity_id
      AND e.entity_status_id = :STATUS
      AND mp.privilege_id = p.privilege_id
    """
