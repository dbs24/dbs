
 alter table tkn_refresh_jwt
    alter column valid_until set not null;

-- tkn_issued_jwt

alter table tkn_issued_jwt
    rename column request_id to issued_to;

alter table tkn_issued_jwt
    add is_revoked tboolean;

alter table tkn_issued_jwt
    add revoke_date tdatetime;

 update tkn_issued_jwt
   set is_revoked = false;

 alter table tkn_issued_jwt
     alter column is_revoked set not null;

 create index if not exists i_tkn_issued_jwt_issued_to
     on tkn_issued_jwt (issued_to);

-- tkn_issued_jwt_arc

 alter table tkn_issued_jwt_arc
     rename column request_id to issued_to;

 alter table tkn_issued_jwt_arc
     add is_revoked tboolean;

 alter table tkn_issued_jwt_arc
     add revoke_date tdatetime;

 update tkn_issued_jwt_arc
 set is_revoked = false;


-- tkn_refresh_jwt

 alter table tkn_refresh_jwt
     add is_revoked tboolean;

 alter table tkn_refresh_jwt
     add revoke_date tdatetime;

 update tkn_refresh_jwt
 set is_revoked = false;

 alter table tkn_refresh_jwt
     alter column is_revoked set not null;

-- tkn_refresh_jwt_arc

 alter table tkn_refresh_jwt_arc
     add is_revoked tboolean;

 alter table tkn_refresh_jwt_arc
     add revoke_date tdatetime;

 update tkn_refresh_jwt_arc
 set is_revoked = false;

-- drop constraint

 alter table tkn_refresh_jwt
     drop constraint if exists ak_tkn_refresh_jwt;

-- stored procedures

 create or replace procedure sp_delete_deprecated_jwt(IN deprecatedate tdatetime)
     language plpgsql
 as
 $$
 begin
     DELETE FROM tkn_refresh_jwt_arc WHERE valid_until <= deprecateDate;
     DELETE FROM tkn_issued_jwt_arc WHERE valid_until <= deprecateDate;
     DELETE FROM tkn_refresh_jwt r WHERE valid_until <= deprecateDate;
     DELETE
     FROM tkn_issued_jwt i
     WHERE i.valid_until <= deprecateDate
       AND NOT exists (SELECT null FROM tkn_refresh_jwt r WHERE r.parent_jwt_id = i.jwt_id);


--commit; -invoke exception in jpa/hibernate
 end;
 $$;


 create or replace procedure sp_arc_deprecated_jwt(IN deprecatedate tdatetime)
     language plpgsql
 as
 $$
 begin
     -- copy 2 arc
     INSERT INTO tkn_issued_jwt_arc  (SELECT * FROM tkn_issued_jwt WHERE valid_until <= deprecatedate  );
     INSERT INTO tkn_refresh_jwt_arc(jwt_id, issue_date, jwt, parent_jwt_id, valid_until, refresh_date, is_revoked, revoke_date)  (SELECT r.jwt_id, r.issue_date, r.jwt, r.parent_jwt_id, r.valid_until, deprecatedate as refresh_date, is_revoked, revoke_date FROM tkn_refresh_jwt r WHERE r.valid_until <= deprecatedate  );

     DELETE FROM tkn_refresh_jwt r WHERE valid_until <= deprecatedate;
     DELETE FROM tkn_issued_jwt i WHERE i.valid_until <= deprecatedate
         AND NOT exists (SELECT null FROM tkn_refresh_jwt r WHERE r.parent_jwt_id = i.jwt_id);

--commit; -invoke exception in jpa/hibernate
 end;
 $$;

 create or replace procedure sp_revoke_jwt(IN jwtOwner tstr200)
     language plpgsql
 as
 $$
 begin
     UPDATE tkn_issued_jwt SET is_revoked = true, revoke_date = now() WHERE issued_to = jwtOwner AND is_revoked = false;
     UPDATE tkn_refresh_jwt SET is_revoked = true, revoke_date = now()
     WHERE parent_jwt_id IN ( SELECT jwt_id FROM tkn_issued_jwt WHERE issued_to = jwtOwner)
       AND is_revoked = false;

--commit; -invoke exception in jpa/hibernate
 end;
 $$;