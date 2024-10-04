create sequence seq_tnk_card_id;

alter sequence seq_tnk_card_id owner to dev_auth_admin;

create domain tbanknotesamt as integer;

alter domain tbanknotesamt owner to dev_auth_admin;

create domain tboolean as boolean;

alter domain tboolean owner to dev_auth_admin;

create domain tchessgamescore as numeric(3, 1);

alter domain tchessgamescore owner to dev_auth_admin;

create domain tcoinsamt as integer;

alter domain tcoinsamt owner to dev_auth_admin;

create domain tcurrencyiso as varchar(3);

alter domain tcurrencyiso owner to dev_auth_admin;

create domain tcurrencystr10 as varchar(10);

alter domain tcurrencystr10 owner to dev_auth_admin;

create domain tdate as date;

alter domain tdate owner to dev_auth_admin;

create domain tdatetime as timestamp;

alter domain tdatetime owner to dev_auth_admin;

create domain tgpscoordinates as point;

alter domain tgpscoordinates owner to dev_auth_admin;

create domain tidbigcode as bigint;

alter domain tidbigcode owner to dev_auth_admin;

create domain tidbytecode as numeric(1, 0);

alter domain tidbytecode owner to dev_auth_admin;

create domain tidcode as integer;

comment on type tidcode is 'Код сущности';

alter domain tidcode owner to dev_auth_admin;

create domain tiduser as integer;

alter domain tiduser owner to dev_auth_admin;

create domain timage as bytea;

alter domain timage owner to dev_auth_admin;

create domain tintcounter as integer;

alter domain tintcounter owner to dev_auth_admin;

create domain tinteger as integer;

alter domain tinteger owner to dev_auth_admin;

create domain titemtype as smallint;

alter domain titemtype owner to dev_auth_admin;

create domain tmoney as numeric(22, 4);

alter domain tmoney owner to dev_auth_admin;

create domain tpercrate as numeric(12, 6);

alter domain tpercrate owner to dev_auth_admin;

create domain tpercrateext as numeric(16, 8);

alter domain tpercrateext owner to dev_auth_admin;

create domain treal as real;

alter domain treal owner to dev_auth_admin;

create domain tstr10 as varchar(10);

alter domain tstr10 owner to dev_auth_admin;

create domain tstr100 as varchar(100);

alter domain tstr100 owner to dev_auth_admin;

create domain tstr10000 as varchar(10000);

alter domain tstr10000 owner to dev_auth_admin;

create domain tstr128 as varchar(128);

alter domain tstr128 owner to dev_auth_admin;

create domain tstr2 as char(2);

alter domain tstr2 owner to dev_auth_admin;

create domain tstr20 as varchar(20);

alter domain tstr20 owner to dev_auth_admin;

create domain tstr200 as varchar(200);

alter domain tstr200 owner to dev_auth_admin;

create domain tstr2000 as varchar(2000);

alter domain tstr2000 owner to dev_auth_admin;

create domain tstr3 as varchar(3);

alter domain tstr3 owner to dev_auth_admin;

create domain tstr30 as varchar(30);

alter domain tstr30 owner to dev_auth_admin;

create domain tstr40 as varchar(40);

alter domain tstr40 owner to dev_auth_admin;

create domain tstr400 as varchar(400);

alter domain tstr400 owner to dev_auth_admin;

create domain tstr50 as varchar(50);

alter domain tstr50 owner to dev_auth_admin;

create domain tstr80 as varchar(80);

alter domain tstr80 owner to dev_auth_admin;

create domain tsumext as numeric(24, 4);

alter domain tsumext owner to dev_auth_admin;

create domain ttext as text;

alter domain ttext owner to dev_auth_admin;

create domain ttime as time;

alter domain ttime owner to dev_auth_admin;

create table tkn_applications
(
    application_id   tinteger not null
        constraint pk_tkn_applications
            primary key,
    application_code tstr100  not null
        constraint ak_tkn_app_code
            unique,
    application_name tstr128  not null
);

comment on table tkn_applications is 'Используемые приложения';

alter table tkn_applications
    owner to dev_auth_admin;

create table tkn_issued_jwt
(
    jwt_id         tidbigcode not null
        constraint pk_tkn_issue_cards
            primary key,
    issue_date     tdatetime  not null,
    valid_until    tdatetime  not null,
    jwt            tstr2000   not null,
    application_id tidcode    not null
        constraint fk_issued_jwt_app_id
            references tkn_applications
            on update restrict on delete restrict,
    issued_to      tstr2000   not null,
    tag            tstr2000,
    is_revoked     tboolean   not null,
    revoke_date    tdatetime
);

comment on table tkn_issued_jwt is 'Использованные refresh-токены';

alter table tkn_issued_jwt
    owner to dev_auth_admin;

create index i_tkn_issued_jwt_issued_to
    on tkn_issued_jwt (issued_to);

create table tkn_issued_jwt_arc
(
    jwt_id         tidbigcode,
    issue_date     tdatetime,
    valid_until    tdatetime,
    jwt            tstr2000,
    application_id tidcode,
    issued_to      tstr2000,
    tag            tstr2000,
    is_revoked     tboolean,
    revoke_date    tdatetime
);

comment on table tkn_issued_jwt_arc is 'Архивные токены';

alter table tkn_issued_jwt_arc
    owner to dev_auth_admin;

create table tkn_refresh_jwt
(
    jwt_id        tidbigcode not null
        constraint pk_tkn_refresh_jwt
            primary key,
    issue_date    tdatetime  not null,
    jwt           tstr2000   not null,
    parent_jwt_id tidbigcode not null
        constraint fk_parent_jwt_id
            references tkn_issued_jwt
            on update restrict on delete restrict,
    valid_until   tdatetime  not null,
    is_revoked    tboolean   not null,
    revoke_date   tdatetime
);

alter table tkn_refresh_jwt
    owner to dev_auth_admin;

create table tkn_refresh_jwt_arc
(
    jwt_id        tidbigcode,
    issue_date    tdatetime,
    refresh_date  tdatetime,
    jwt           tstr2000,
    parent_jwt_id tidbigcode,
    valid_until   tdatetime,
    is_revoked    tboolean,
    revoke_date   tdatetime
);

alter table tkn_refresh_jwt_arc
    owner to dev_auth_admin;

create procedure sp_delete_deprecated_jwt(IN deprecatedate tdatetime)
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

alter procedure sp_delete_deprecated_jwt(tdatetime) owner to dev_auth_admin;

create procedure sp_arc_deprecated_jwt(IN deprecatedate tdatetime)
    language plpgsql
as
$$
begin
    -- copy 2 arc
    INSERT INTO tkn_issued_jwt_arc (SELECT i.*
                                    FROM tkn_issued_jwt i,
                                         tkn_refresh_jwt r
                                    WHERE (i.valid_until <= deprecatedate OR i.is_revoked = true)
                                      AND i.jwt_id = r.parent_jwt_id
                                      AND (r.valid_until <= deprecatedate OR r.is_revoked = true));
    INSERT INTO tkn_refresh_jwt_arc(jwt_id, issue_date, jwt, parent_jwt_id, valid_until, refresh_date, is_revoked,
                                    revoke_date) (SELECT r.jwt_id,
                                                         r.issue_date,
                                                         r.jwt,
                                                         r.parent_jwt_id,
                                                         r.valid_until,
                                                         deprecatedate as refresh_date,
                                                         is_revoked,
                                                         revoke_date
                                                  FROM tkn_refresh_jwt r
                                                  WHERE r.valid_until <= deprecatedate
                                                     OR is_revoked = true);

    DELETE FROM tkn_refresh_jwt r WHERE r.valid_until <= deprecatedate OR r.is_revoked = true;
    DELETE
    FROM tkn_issued_jwt i
    WHERE (i.valid_until <= deprecatedate OR i.is_revoked = true)
      AND NOT exists (SELECT null FROM tkn_refresh_jwt r WHERE r.parent_jwt_id = i.jwt_id);

--commit; -invoke exception in jpa/hibernate
end;
$$;

alter procedure sp_arc_deprecated_jwt(tdatetime) owner to dev_auth_admin;

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
