alter table tkn_applications
    alter column application_code type tstr100 using application_code::tstr100;


drop procedure sp_revoke_jwt(tstr200);

create procedure sp_revoke_jwt(IN p_login tstr200, IN p_applicationid tidcode)
    language plpgsql
as
$$
begin
    UPDATE tkn_issued_jwt
    SET is_revoked  = true,
        revoke_date = now()
    WHERE issued_to = p_login
      AND application_id = p_applicationid
      AND is_revoked = false;
    UPDATE tkn_refresh_jwt
    SET is_revoked  = true,
        revoke_date = now()
    WHERE parent_jwt_id IN
          (SELECT jwt_id FROM tkn_issued_jwt WHERE issued_to = p_login AND application_id = p_applicationid)
      AND is_revoked = false;

--commit; -invoke exception in jpa/hibernate
end;
$$;

alter procedure sp_revoke_jwt(tstr200, tidcode) owner to dev_auth_admin;
