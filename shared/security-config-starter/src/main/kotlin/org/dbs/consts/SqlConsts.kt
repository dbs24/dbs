package org.dbs.consts

const val SQL_CREATE_ACCESS_TOKEN_TABLES = """
    
    CREATE TABLE IF NOT EXISTS core_issued_jwt (
        jwt_id       tidbigcode DEFAULT nextval('seq_action_id') NOT NULL
            CONSTRAINT pk_core_issued_jwt 
                PRIMARY KEY,
        issue_date   tdatetime  NOT NULL,
        valid_until  tdatetime  NOT NULL,
        jwt          tstr2000   NOT NULL,
        issued_to    tstr2000   NOT NULL,
        tag          tstr2000,
        is_revoked   tboolean   NOT NULL,
        revoke_date  tdatetime
    );
    
    COMMENT ON TABLE core_issued_jwt IS 'Used Access Tokens';    
    
    CREATE INDEX IF NOT EXISTS i_core_issued_jwt_jwt
        ON core_issued_jwt (jwt);
        
    CREATE TABLE IF NOT EXISTS core_refresh_jwt (
        jwt_id        tidbigcode DEFAULT nextval('seq_action_id') NOT NULL
            CONSTRAINT pk_core_refresh_jwt 
                PRIMARY KEY,
        issue_date    tdatetime  NOT NULL,
        jwt           tstr2000   NOT NULL,
        parent_jwt_id tidbigcode NOT NULL
            CONSTRAINT fk_parent_jwt_id 
                REFERENCES core_issued_jwt
                ON UPDATE RESTRICT ON DELETE RESTRICT,
        valid_until   tdatetime  NOT NULL,
        is_revoked    tboolean   NOT NULL,
        revoke_date   tdatetime
    );
    
    
    CREATE INDEX IF NOT EXISTS i_core_refresh_jwt_jwt
        ON core_refresh_jwt (jwt);   
    
    COMMENT ON TABLE core_refresh_jwt IS 'Used Refresh tokens';          
        
"""