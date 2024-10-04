create or replace procedure sp_arc_deprecated_jwt(IN deprecatedate tdatetime)
    language plpgsql
as
$$
begin
    -- copy 2 arc
    INSERT INTO tkn_issued_jwt_arc  (SELECT * FROM tkn_issued_jwt WHERE valid_until <= deprecatedate OR is_revoked = true );
    INSERT INTO tkn_refresh_jwt_arc(jwt_id, issue_date, jwt, parent_jwt_id, valid_until, refresh_date, is_revoked, revoke_date)  (SELECT r.jwt_id, r.issue_date, r.jwt, r.parent_jwt_id, r.valid_until, deprecatedate as refresh_date, is_revoked, revoke_date FROM tkn_refresh_jwt r WHERE r.valid_until <= deprecatedate OR is_revoked = true );

    DELETE FROM tkn_refresh_jwt r WHERE valid_until <= deprecatedate OR is_revoked = true;
    DELETE FROM tkn_issued_jwt i WHERE (i.valid_until <= deprecatedate
        AND NOT exists (SELECT null FROM tkn_refresh_jwt r WHERE r.parent_jwt_id = i.jwt_id)) OR is_revoked = true;

--commit; -invoke exception in jpa/hibernate
end;
$$;
